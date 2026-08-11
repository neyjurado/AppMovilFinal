package com.example.appmovilfinal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 1. Definición de la Base de Datos
@Database(entities = [Gasto::class], version = 1, exportSchema = false)
abstract class GastoDatabase : RoomDatabase() {

    // 2. Conexión con el DAO
    abstract fun gastoDao(): GastoDao

    // 3. Patrón Singleton para evitar múltiples instancias
    companion object {
        @Volatile
        private var INSTANCE: GastoDatabase? = null

        fun getDatabase(context: Context): GastoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GastoDatabase::class.java,
                    "gasto_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}