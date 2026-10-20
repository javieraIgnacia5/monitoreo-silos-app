
package com.example.monitoreosilos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movements")
data class MovementEntity(
    @PrimaryKey
    val id: Long,

    val siloId: String,
    val siloName: String,
    val grain: String,
    val type: String,
    val tons: Double,
    val date: String,
    val notes: String
)
