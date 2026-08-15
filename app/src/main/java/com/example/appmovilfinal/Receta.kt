package com.example.appmovilfinal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recetas")
data class Receta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombrePlatillo: String,
    val ingredientes: String,
    val tiempoPreparacion: Int,
    val instrucciones: String,
    val imagenUri: String? = null // nulo si no guarda foto
)