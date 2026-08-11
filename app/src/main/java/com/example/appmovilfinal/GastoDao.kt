package com.example.appmovilfinal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoDao {

    @Insert
    suspend fun insertarGasto(gasto: Gasto)

    // Usamos Flow  para leer la lista de gastos
    // Los ordenamos por ID de forma descendente para ver los más recientes primero
    @Query("SELECT * FROM gastos ORDER BY id DESC")
    fun obtenerTodosLosGastos(): Flow<List<Gasto>>
}
