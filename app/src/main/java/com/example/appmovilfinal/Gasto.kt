package com.example.appmovilfinal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gastos")
data class Gasto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Se genera automáticamente al insertar en la base de datos

    val nombreComercio: String, // Ej: "Netflix", "Uber" - Lo usaremos con DeBounce API
    val monto: Double,
    val categoria: String,
    val fecha: String,
    val rutaFotoRecibo: String = "" // Lo dejamos por defecto vacío hasta usar la cámara
)