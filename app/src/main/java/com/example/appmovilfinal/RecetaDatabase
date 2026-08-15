package com.example.appmovilfinal

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 1. Definición de la Base de Datos con la nueva Entidad
@Database(entities = [Receta::class], version = 1, exportSchema = false)
abstract class RecetaDatabase : RoomDatabase() {

    // 2. Conexión con el nuevo DAO
    abstract fun recetaDao(): RecetaDao

    // 3. Patrón Singleton para evitar múltiples instancias
    companion object {
        @Volatile
        private var INSTANCE: RecetaDatabase? = null

        fun getDatabase(context: Context): RecetaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecetaDatabase::class.java,
                    "receta_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}