
package com.example.monitoreosilos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "silos")
data class SiloEntity(
    @PrimaryKey
    val id: String,

    val name: String,
    val grain: String,
    val farm: String,
    val capacityTon: Int,
    val currentTon: Double,
    val updatedAt: String,

    // Ubicación ingresada manualmente
    val locationAddress: String = "",

    // Coordenadas obtenidas mediante GPS
    val latitude: Double? = null,
    val longitude: Double? = null
)
