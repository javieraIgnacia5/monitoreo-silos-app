
package com.example.monitoreosilos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovementDao {

    @Query("SELECT * FROM movements ORDER BY id DESC")
    fun obtenerMovimientos(): Flow<List<MovementEntity>>

    @Query("SELECT * FROM movements WHERE siloId = :siloId ORDER BY id DESC")
    fun obtenerMovimientosPorSilo(siloId: String): Flow<List<MovementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarMovimiento(movimiento: MovementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarMovimientos(movimientos: List<MovementEntity>)

    @Query("DELETE FROM movements WHERE id = :movimientoId")
    suspend fun eliminarMovimiento(movimientoId: Long)
}

