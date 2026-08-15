package com.example.appmovilfinal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecetaDao {

    @Insert
    suspend fun insertarReceta(receta: Receta)

    @Query("SELECT * FROM recetas ORDER BY id DESC")
    fun obtenerTodasLasRecetas(): Flow<List<Receta>>
}