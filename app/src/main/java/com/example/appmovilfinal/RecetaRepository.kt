package com.example.appmovilfinal

import kotlinx.coroutines.flow.Flow

class RecetaRepository(private val recetaDao: RecetaDao) {

    // 1. Variable reactiva que expone el catálogo de recetas
    val todasLasRecetas: Flow<List<Receta>> = recetaDao.obtenerTodasLasRecetas()

    // 2. Función asíncrona para guardar una nueva receta
    suspend fun insertar(receta: Receta) {
        recetaDao.insertarReceta(receta)
    }

    // Función para traer las recetas de internet usando Retrofit
    suspend fun obtenerRecetasTopRemotas(): List<RecetaRemota> {
        return RetrofitClient.api.obtenerRecetasTop().recipes
    }
}