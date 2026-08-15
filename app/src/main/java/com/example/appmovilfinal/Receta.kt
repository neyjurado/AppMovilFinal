package com.example.appmovilfinal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recetas")
data class Receta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombrePlatillo: String,   // Ej: "Ceviche", "Lasagna"
    val ingredientes: String,     // Ej: "Pescado, limón, cebolla" (Lo guardamos como texto simple)
    val tiempoPreparacion: Int,   // Tiempo en minutos, útil para filtrar
    val instrucciones: String,    // Los pasos a seguir
    val rutaImagen: String = "",  // URL de internet o ruta de la cámara
    val esFavorita: Boolean = false // Perfecto para agregar un botón de "Me gusta"
)