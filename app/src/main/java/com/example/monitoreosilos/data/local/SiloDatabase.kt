
package com.example.monitoreosilos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        SiloEntity::class,
        MovementEntity::class
    ],
    version = 2,
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

        // Migración de la versión 1 a la versión 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {

                // Dirección o referencia ingresada manualmente
                db.execSQL(
                    "ALTER TABLE silos ADD COLUMN locationAddress TEXT NOT NULL DEFAULT ''"
                )

                // Coordenadas GPS opcionales
                db.execSQL(
                    "ALTER TABLE silos ADD COLUMN latitude REAL"
                )

                db.execSQL(
                    "ALTER TABLE silos ADD COLUMN longitude REAL"
                )
            }
        }

        @Volatile
        private var INSTANCE: SiloDatabase? = null

        fun getDatabase(context: Context): SiloDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SiloDatabase::class.java,
                    "silocontrol_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
