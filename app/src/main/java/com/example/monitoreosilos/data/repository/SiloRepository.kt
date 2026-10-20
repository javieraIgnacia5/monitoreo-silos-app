
package com.example.monitoreosilos.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.room.withTransaction
import com.example.monitoreosilos.data.SampleData
import com.example.monitoreosilos.data.local.SiloDatabase
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
import org.json.JSONArray
import org.json.JSONObject

object SiloRepository {

    private lateinit var prefs: SharedPreferences

    private lateinit var database: SiloDatabase

    private val repositoryScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

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

        // Mantener los datos actuales de la aplicación.
        _silos.value = loadSilos() ?: SampleData.silos
        _movements.value =
            loadMovements() ?: SampleData.initialMovements()

        // Comprobar la migración a Room en segundo plano.
        repositoryScope.launch {
            try {
                migrarDatosARoom()

                val cantidadSilos =
                    database.siloDao().contarSilos()

                val cantidadMovimientos =
                    database.movementDao()
                        .obtenerMovimientos()
                        .first()
                        .size

                Log.d(
                    "SiloRepository",
                    "Cantidad de silos guardados en Room: $cantidadSilos"
                )

                Log.d(
                    "SiloRepository",
                    "Cantidad de movimientos guardados en Room: $cantidadMovimientos"
                )

                Log.d(
                    "SiloRepository",
                    "Migración a Room completada"
                )

            } catch (e: Exception) {
                Log.e(
                    "SiloRepository",
                    "Error al migrar o consultar los datos de Room",
                    e
                )
            }
        }
    }

    fun siloById(id: String): Silo? {
        return _silos.value.firstOrNull { it.id == id }
    }

    fun registerMovement(
        siloId: String,
        type: MovementType,
        tons: Double,
        date: String,
        notes: String,
        grain: String
    ) {
        val silo = siloById(siloId) ?: return

        val newLevel = when (type) {
            MovementType.INGRESO -> silo.currentTon + tons
            MovementType.SALIDA -> silo.currentTon - tons
        }

        _silos.value = _silos.value.map {
            if (it.id == siloId) {
                it.copy(
                    currentTon = newLevel,
                    updatedAt = DateUtils.now()
                )
            } else {
                it
            }
        }

        val nextId =
            (_movements.value.maxOfOrNull { it.id } ?: 0L) + 1

        val movement = Movement(
            nextId,
            silo.id,
            silo.name,
            grain,
            type,
            tons,
            date,
            notes
        )

        _movements.value = listOf(movement) + _movements.value

        persist()
    }

    // ---------- Guardado local en SharedPreferences ----------

    private fun persist() {
        val silosJson = JSONArray()

        _silos.value.forEach { silo ->
            silosJson.put(
                JSONObject().apply {
                    put("id", silo.id)
                    put("name", silo.name)
                    put("grain", silo.grain)
                    put("farm", silo.farm)
                    put("capacity", silo.capacityTon)
                    put("current", silo.currentTon)
                    put("updatedAt", silo.updatedAt)
                }
            )
        }

        val movJson = JSONArray()

        _movements.value.forEach { movement ->
            movJson.put(
                JSONObject().apply {
                    put("id", movement.id)
                    put("siloId", movement.siloId)
                    put("siloName", movement.siloName)
                    put("grain", movement.grain)
                    put("type", movement.type.name)
                    put("tons", movement.tons)
                    put("date", movement.date)
                    put("notes", movement.notes)
                }
            )
        }

        prefs.edit()
            .putString("silos", silosJson.toString())
            .putString("movements", movJson.toString())
            .apply()
    }

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
            Log.e("SiloRepository", "Error al cargar silos", e)
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
                "Error al cargar movimientos",
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

        val silosGuardados = loadSilos() ?: SampleData.silos

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
    }
}
