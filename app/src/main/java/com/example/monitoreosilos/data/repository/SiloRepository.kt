
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
import org.json.JSONObject

object SiloRepository {

    private lateinit var prefs: SharedPreferences
    private lateinit var database: SiloDatabase

    private val repositoryScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    // Evita que dos movimientos se registren al mismo tiempo.
    private val movementMutex = Mutex()

    private val _silos = MutableStateFlow<List<Silo>>(emptyList())
    val silos: StateFlow<List<Silo>> = _silos.asStateFlow()

    private val _movements = MutableStateFlow<List<Movement>>(emptyList())
    val movements: StateFlow<List<Movement>> = _movements.asStateFlow()

    /** Se llama una sola vez desde MainActivity. */
    fun init(context: Context) {
        if (::prefs.isInitialized) return

        prefs = context.applicationContext.getSharedPreferences(
            "silocontrol",
            Context.MODE_PRIVATE
        )

        database = SiloDatabase.getDatabase(context)

        // Datos temporales mientras Room termina de cargar.
        _silos.value = loadSilos() ?: SampleData.silos
        _movements.value =
            loadMovements() ?: SampleData.initialMovements()

        repositoryScope.launch {
            try {
                // Proteger la migración y la carga inicial.
                movementMutex.withLock {
                    migrarDatosARoom()
                    cargarDatosDesdeRoom()
                }

                Log.d(
                    "SiloRepository",
                    "SiloControl cargó los datos desde Room correctamente"
                )

            } catch (e: Exception) {
                Log.e(
                    "SiloRepository",
                    "Error al cargar los datos desde Room",
                    e
                )
            }
        }
    }

    fun siloById(id: String): Silo? {
        return _silos.value.firstOrNull { it.id == id }
    }

    // ---------- Registro de movimientos en Room ----------

    suspend fun registerMovement(
        siloId: String,
        type: MovementType,
        tons: Double,
        date: String,
        notes: String,
        grain: String
    ) = movementMutex.withLock {

        require(tons.isFinite() && tons > 0) {
            "La cantidad debe ser mayor que cero"
        }

        // Esperar a que la migración esté completada.
        migrarDatosARoom()

        // La lectura, el cálculo y el guardado se hacen
        // dentro de una única transacción.
        database.withTransaction {

            val siloEntity = database.siloDao()
                .obtenerSiloPorId(siloId)
                ?: throw IllegalArgumentException(
                    "No se encontró el silo seleccionado"
                )

            val silo = siloEntity.toDomain()

            val newLevel = when (type) {
                MovementType.INGRESO -> silo.currentTon + tons
                MovementType.SALIDA -> silo.currentTon - tons
            }

            require(
                newLevel.isFinite() &&
                        newLevel >= 0 &&
                        newLevel <= silo.capacityTon
            ) {
                "La cantidad supera los límites del silo"
            }

            val siloActualizado = silo.copy(
                currentTon = newLevel,
                updatedAt = DateUtils.now()
            )

            val movimientosActuales = database.movementDao()
                .obtenerMovimientos()
                .first()

            val nextId = (
                    movimientosActuales.maxOfOrNull { it.id } ?: 0L
                    ) + 1L

            val movimiento = Movement(
                nextId,
                silo.id,
                silo.name,
                grain,
                type,
                tons,
                date,
                notes
            )

            // Ambas operaciones se guardan juntas.
            database.siloDao().guardarSilo(
                siloActualizado.toEntity()
            )

            database.movementDao().guardarMovimiento(
                movimiento.toEntity()
            )
        }

        // Actualizar las pantallas solamente después
        // de confirmar el guardado.
        cargarDatosDesdeRoom()

        Log.d(
            "SiloRepository",
            "Movimiento guardado correctamente en Room"
        )
    }

    // ---------- Datos anteriores en SharedPreferences ----------

    private fun loadSilos(): List<Silo>? {
        val raw = prefs.getString("silos", null) ?: return null

        return try {
            val arr = JSONArray(raw)

            (0 until arr.length()).map { index ->
                val o = arr.getJSONObject(index)

                Silo(
                    o.getString("id"),
                    o.getString("name"),
                    o.getString("grain"),
                    o.getString("farm"),
                    o.getInt("capacity"),
                    o.getDouble("current"),
                    o.getString("updatedAt")
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
        val raw = prefs.getString("movements", null) ?: return null

        return try {
            val arr = JSONArray(raw)

            (0 until arr.length()).map { index ->
                val o = arr.getJSONObject(index)

                Movement(
                    o.getLong("id"),
                    o.getString("siloId"),
                    o.getString("siloName"),
                    o.getString("grain"),
                    MovementType.valueOf(o.getString("type")),
                    o.getDouble("tons"),
                    o.getString("date"),
                    o.getString("notes")
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

        if (prefs.getBoolean("room_migration_completed", false)) {
            return
        }

        val silosGuardados =
            loadSilos() ?: SampleData.silos

        val movimientosGuardados =
            loadMovements() ?: SampleData.initialMovements()

        database.withTransaction {

            val cantidadSilos =
                database.siloDao().contarSilos()

            if (cantidadSilos == 0) {

                database.siloDao().guardarSilos(
                    silosGuardados.map { it.toEntity() }
                )

                database.movementDao().guardarMovimientos(
                    movimientosGuardados.map { it.toEntity() }
                )
            }
        }

        val guardado = prefs.edit()
            .putBoolean("room_migration_completed", true)
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

    // ---------- Lectura de datos desde Room ----------

    private suspend fun cargarDatosDesdeRoom() {

        val silosGuardados = database.siloDao()
            .obtenerTodosLosSilos()
            .map { it.toDomain() }

        val movimientosGuardados = database.movementDao()
            .obtenerMovimientos()
            .first()
            .map { it.toDomain() }

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
