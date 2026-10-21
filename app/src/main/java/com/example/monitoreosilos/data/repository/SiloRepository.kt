
package com.example.monitoreosilos.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.room.withTransaction
import com.example.monitoreosilos.data.SampleData
import com.example.monitoreosilos.data.local.SiloDatabase
import com.example.monitoreosilos.data.local.toDomain
import com.example.monitoreosilos.data.local.toEntity
import com.example.monitoreosilos.domain.model.Movement
import com.example.monitoreosilos.domain.model.MovementType
import com.example.monitoreosilos.domain.model.Silo
import com.example.monitoreosilos.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray

object SiloRepository {

    private lateinit var prefs: SharedPreferences
    private lateinit var database: SiloDatabase

    private val repositoryScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    // Evita registrar dos movimientos simultáneamente.
    private val movementMutex = Mutex()

    // Indica si Room terminó de cargar correctamente.
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _silos = MutableStateFlow<List<Silo>>(emptyList())
    val silos: StateFlow<List<Silo>> = _silos.asStateFlow()

    private val _movements = MutableStateFlow<List<Movement>>(emptyList())
    val movements: StateFlow<List<Movement>> = _movements.asStateFlow()

    // ---------- Inicialización ----------

    fun init(context: Context) {
        if (::prefs.isInitialized) return

        prefs = context.applicationContext.getSharedPreferences(
            "silocontrol",
            Context.MODE_PRIVATE
        )

        database = SiloDatabase.getDatabase(
            context.applicationContext
        )

        repositoryScope.launch {
            try {
                movementMutex.withLock {
                    migrarDatosARoom()
                    cargarDatosDesdeRoom()
                    _isReady.value = true
                }

                Log.d(
                    "SiloRepository",
                    "SiloControl cargó los datos desde Room correctamente"
                )

            } catch (e: Exception) {
                _isReady.value = false

                Log.e(
                    "SiloRepository",
                    "Error al cargar los datos desde Room",
                    e
                )
            }
        }
    }

    // ---------- Consulta de silos ----------

    fun siloById(id: String): Silo? {
        return _silos.value.firstOrNull {
            it.id == id
        }

    }

    suspend fun addSilo(
        name: String,
        grain: String,
        farm: String,
        capacity: Int,
        current: Double
    ) = movementMutex.withLock {

        check(::database.isInitialized && _isReady.value) {
            "La base de datos todavía no está lista"
        }

        require(name.isNotBlank()) {
            "Debes ingresar el nombre del silo"
        }

        require(grain.isNotBlank() && farm.isNotBlank()) {
            "Debes completar el grano y el predio"
        }

        require(capacity > 0 && current.isFinite() &&
                current >= 0 && current <= capacity) {
            "La cantidad debe estar entre cero y la capacidad máxima"
        }

        val nuevoId = java.util.UUID.randomUUID().toString()

        val nuevoSilo = Silo(
            id = nuevoId,
            name = name.trim(),
            grain = grain.trim(),
            farm = farm.trim(),
            capacityTon = capacity,
            currentTon = current,
            updatedAt = DateUtils.now()
        )

        database.siloDao().guardarSilo(nuevoSilo.toEntity())

        cargarDatosDesdeRoom()

        Log.d("SiloRepository", "Nuevo silo guardado: ${nuevoSilo.name}")
    }


    // ---------- Registro de movimientos ----------

    suspend fun registerMovement(
        siloId: String,
        type: MovementType,
        tons: Double,
        date: String,
        notes: String,
        grain: String
    ) = movementMutex.withLock {

        check(::database.isInitialized) {
            "La base de datos todavía no está inicializada"
        }

        require(tons.isFinite() && tons > 0) {
            "La cantidad debe ser mayor que cero"
        }

        // Completar la migración si todavía está pendiente.
        migrarDatosARoom()

        // Leer, calcular y guardar en una sola transacción.
        database.withTransaction {

            val siloEntity = database.siloDao()
                .obtenerSiloPorId(siloId)
                ?: throw IllegalArgumentException(
                    "No se encontró el silo seleccionado"
                )

            val silo = siloEntity.toDomain()

            val nuevoNivel = when (type) {
                MovementType.INGRESO ->
                    silo.currentTon + tons

                MovementType.SALIDA ->
                    silo.currentTon - tons
            }

            require(
                nuevoNivel.isFinite() &&
                        nuevoNivel >= 0 &&
                        nuevoNivel <= silo.capacityTon
            ) {
                "La cantidad supera los límites del silo"
            }

            val siloActualizado = silo.copy(
                currentTon = nuevoNivel,
                updatedAt = DateUtils.now()
            )

            val movimientosActuales =
                database.movementDao()
                    .obtenerMovimientos()
                    .first()

            val ultimoId =
                movimientosActuales.maxOfOrNull {
                    it.id
                } ?: 0L

            check(ultimoId < Long.MAX_VALUE) {
                "No se puede generar otro ID de movimiento"
            }

            val nuevoId = ultimoId + 1L

            val movimiento = Movement(
                id = nuevoId,
                siloId = silo.id,
                siloName = silo.name,
                grain = grain,
                type = type,
                tons = tons,
                date = date,
                notes = notes
            )

            // Guardar el nivel actualizado del silo.
            database.siloDao().guardarSilo(
                siloActualizado.toEntity()
            )

            // Guardar el movimiento en el historial.
            database.movementDao().guardarMovimiento(
                movimiento.toEntity()
            )
        }

        // Actualizar las pantallas después de guardar.
        cargarDatosDesdeRoom()

        Log.d(
            "SiloRepository",
            "Movimiento guardado correctamente en Room"
        )
    }

    // ---------- Lectura de datos antiguos ----------

    private fun loadSilos(): List<Silo>? {
        val raw = prefs.getString("silos", null)
            ?: return null

        return try {
            val arr = JSONArray(raw)

            (0 until arr.length()).map { index ->
                val o = arr.getJSONObject(index)

                Silo(
                    id = o.getString("id"),
                    name = o.getString("name"),
                    grain = o.getString("grain"),
                    farm = o.getString("farm"),
                    capacityTon = o.getInt("capacity"),
                    currentTon = o.getDouble("current"),
                    updatedAt = o.getString("updatedAt")
                )
            }

        } catch (e: Exception) {
            Log.e(
                "SiloRepository",
                "Error al cargar silos anteriores",
                e
            )
            null
        }
    }

    private fun loadMovements(): List<Movement>? {
        val raw = prefs.getString("movements", null)
            ?: return null

        return try {
            val arr = JSONArray(raw)

            (0 until arr.length()).map { index ->
                val o = arr.getJSONObject(index)

                Movement(
                    id = o.getLong("id"),
                    siloId = o.getString("siloId"),
                    siloName = o.getString("siloName"),
                    grain = o.getString("grain"),
                    type = MovementType.valueOf(
                        o.getString("type")
                    ),
                    tons = o.getDouble("tons"),
                    date = o.getString("date"),
                    notes = o.getString("notes")
                )
            }

        } catch (e: Exception) {
            Log.e(
                "SiloRepository",
                "Error al cargar movimientos anteriores",
                e
            )
            null
        }
    }

    // ---------- Migración inicial a Room ----------

    private suspend fun migrarDatosARoom() {

        if (
            prefs.getBoolean(
                "room_migration_completed",
                false
            )
        ) {
            return
        }

        val silosGuardados =
            loadSilos() ?: SampleData.silos

        val movimientosGuardados =
            loadMovements()
                ?: SampleData.initialMovements()

        database.withTransaction {

            val cantidadSilos =
                database.siloDao().contarSilos()

            if (cantidadSilos == 0) {

                database.siloDao().guardarSilos(
                    silosGuardados.map {
                        it.toEntity()
                    }
                )

                database.movementDao().guardarMovimientos(
                    movimientosGuardados.map {
                        it.toEntity()
                    }
                )
            }
        }

        val guardado = prefs.edit()
            .putBoolean(
                "room_migration_completed",
                true
            )
            .commit()

        if (!guardado) {
            throw IllegalStateException(
                "No se pudo confirmar la migración a Room"
            )
        }

        Log.d(
            "SiloRepository",
            "Migración inicial a Room completada"
        )
    }

    // ---------- Carga de datos desde Room ----------

    private suspend fun cargarDatosDesdeRoom() {

        val silosGuardados = database.siloDao()
            .obtenerTodosLosSilos()
            .map {
                it.toDomain()
            }

        val movimientosGuardados =
            database.movementDao()
                .obtenerMovimientos()
                .first()
                .map {
                    it.toDomain()
                }

        _silos.value = silosGuardados
        _movements.value = movimientosGuardados

        Log.d(
            "SiloRepository",
            "Datos cargados desde Room: " +
                    "${silosGuardados.size} silos y " +
                    "${movimientosGuardados.size} movimientos"
        )
    }
}
