
package com.example.monitoreosilos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction


@Database(
    entities = [
        SiloEntity::class,
        MovementEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SiloDatabase : RoomDatabase() {

    abstract fun siloDao(): SiloDao

    abstract fun movementDao(): MovementDao

    suspend fun guardarMovimientoConSilo(
        silo: SiloEntity,
        movimiento: MovementEntity
    ) {
        withTransaction {
            siloDao().guardarSilo(silo)
            movementDao().guardarMovimiento(movimiento)
        }
    }


    companion object {

        @Volatile
        private var INSTANCE: SiloDatabase? = null

        fun getDatabase(context: Context): SiloDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SiloDatabase::class.java,
                    "silocontrol_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
