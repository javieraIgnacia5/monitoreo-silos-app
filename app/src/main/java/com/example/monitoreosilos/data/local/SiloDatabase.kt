
package com.example.monitoreosilos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [SiloEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SiloDatabase : RoomDatabase() {

    abstract fun siloDao(): SiloDao

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
