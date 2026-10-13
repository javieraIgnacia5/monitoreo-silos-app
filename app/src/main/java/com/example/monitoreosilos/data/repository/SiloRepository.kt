package com.example.monitoreosilos.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.monitoreosilos.data.SampleData
import com.example.monitoreosilos.domain.model.Movement
import com.example.monitoreosilos.domain.model.MovementType
import com.example.monitoreosilos.domain.model.Silo
import com.example.monitoreosilos.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

object SiloRepository {

    private lateinit var prefs: SharedPreferences

    private val _silos = MutableStateFlow<List<Silo>>(emptyList())
    val silos: StateFlow<List<Silo>> = _silos.asStateFlow()

    private val _movements = MutableStateFlow<List<Movement>>(emptyList())
    val movements: StateFlow<List<Movement>> = _movements.asStateFlow()

    /** Se llama una sola vez desde MainActivity. */
    fun init(context: Context) {
        if (::prefs.isInitialized) return
        prefs = context.applicationContext.getSharedPreferences("silocontrol", Context.MODE_PRIVATE)
        _silos.value = loadSilos() ?: SampleData.silos
        _movements.value = loadMovements() ?: SampleData.initialMovements()
    }

    fun siloById(id: String): Silo? = _silos.value.firstOrNull { it.id == id }

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
            if (it.id == siloId) it.copy(currentTon = newLevel, updatedAt = DateUtils.now()) else it
        }
        val nextId = (_movements.value.maxOfOrNull { it.id } ?: 0L) + 1
        val movement = Movement(nextId, silo.id, silo.name, grain, type, tons, date, notes)
        _movements.value = listOf(movement) + _movements.value
        persist()
    }

    // ---------- Guardado local (JSON en SharedPreferences) ----------

    private fun persist() {
        val silosJson = JSONArray()
        _silos.value.forEach {
            silosJson.put(JSONObject().apply {
                put("id", it.id); put("name", it.name); put("grain", it.grain)
                put("farm", it.farm); put("capacity", it.capacityTon)
                put("current", it.currentTon); put("updatedAt", it.updatedAt)
            })
        }
        val movJson = JSONArray()
        _movements.value.forEach {
            movJson.put(JSONObject().apply {
                put("id", it.id); put("siloId", it.siloId); put("siloName", it.siloName)
                put("grain", it.grain); put("type", it.type.name); put("tons", it.tons)
                put("date", it.date); put("notes", it.notes)
            })
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
            (0 until arr.length()).map {
                val o = arr.getJSONObject(it)
                Silo(
                    o.getString("id"), o.getString("name"), o.getString("grain"),
                    o.getString("farm"), o.getInt("capacity"), o.getDouble("current"),
                    o.getString("updatedAt")
                )
            }
        } catch (e: Exception) { null }
    }

    private fun loadMovements(): List<Movement>? {
        val raw = prefs.getString("movements", null) ?: return null
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map {
                val o = arr.getJSONObject(it)
                Movement(
                    o.getLong("id"), o.getString("siloId"), o.getString("siloName"),
                    o.getString("grain"), MovementType.valueOf(o.getString("type")),
                    o.getDouble("tons"), o.getString("date"), o.getString("notes")
                )
            }
        } catch (e: Exception) { null }
    }
}