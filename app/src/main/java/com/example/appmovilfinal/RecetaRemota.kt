package com.example.appmovilfinal

data class RespuestaRecetasApi(
    val recipes: List<RecetaRemota>
)

data class RecetaRemota(
    val id: Int,
    val name: String,
    val ingredients: List<String>,
    val prepTimeMinutes: Int,
    val instructions: List<String>,
    val image: String
)