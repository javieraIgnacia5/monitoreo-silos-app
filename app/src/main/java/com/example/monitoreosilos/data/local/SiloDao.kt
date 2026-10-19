
package com.example.monitoreosilos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SiloDao {

    @Query("SELECT * FROM silos")
    fun obtenerSilos(): Flow<List<SiloEntity>>

    @Query("SELECT * FROM silos WHERE id = :siloId LIMIT 1")
    suspend fun obtenerSiloPorId(siloId: String): SiloEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarSilo(silo: SiloEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarSilos(silos: List<SiloEntity>)

    @Query("DELETE FROM silos WHERE id = :siloId")
    suspend fun eliminarSilo(siloId: String)
}
