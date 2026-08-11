package com.example.appmovilfinal

import kotlinx.coroutines.flow.Flow

class GastoRepository(private val gastoDao: GastoDao) {

    // 1. Variable reactiva que expone la lista de gastos
    val todosLosGastos: Flow<List<Gasto>> = gastoDao.obtenerTodosLosGastos()

    // 2. Función asíncrona para guardar un gasto
    suspend fun insertar(gasto: Gasto) {
        gastoDao.insertarGasto(gasto)
    }
}