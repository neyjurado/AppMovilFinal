package com.example.appmovilfinal

import kotlinx.coroutines.flow.Flow

class RecetaRepository(private val recetaDao: RecetaDao) {

    // 1. Variable reactiva que expone el catálogo de recetas
    val todasLasRecetas: Flow<List<Receta>> = recetaDao.obtenerTodasLasRecetas()

    // 2. Función asíncrona para guardar una nueva receta
    suspend fun insertar(receta: Receta) {
        recetaDao.insertarReceta(receta)
    }

    // FUNCIÓN PARA BORRAR
    // 3. Función asíncrona para eliminar una receta existente
    suspend fun borrar(receta: Receta) {
        recetaDao.borrarReceta(receta)
    }

    // Función para traer las recetas de internet usando Retrofit
    suspend fun obtenerRecetasTopRemotas(): List<RecetaRemota> {
        return RetrofitClient.api.obtenerRecetasTop().recipes
    }
}