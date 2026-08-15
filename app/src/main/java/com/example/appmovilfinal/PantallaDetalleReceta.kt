package com.example.appmovilfinal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleReceta(navController: NavController, viewModel: RecetaViewModel, recetaId: Int) {
    // Buscamos en la lista la receta que coincida con el ID que nos enviaron
    val listaRecetas by viewModel.recetas.collectAsState()
    val recetaSeleccionada = listaRecetas.find { it.id == recetaId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(recetaSeleccionada?.nombrePlatillo ?: "Detalle de Receta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (recetaSeleccionada != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- NUEVO: Imagen principal de la receta ---
                if (recetaSeleccionada.imagenUri != null) {
                    AsyncImage(
                        model = recetaSeleccionada.imagenUri,
                        contentDescription = "Foto detallada de ${recetaSeleccionada.nombrePlatillo}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Text(
                    text = "Tiempo de preparación: ${recetaSeleccionada.tiempoPreparacion} minutos",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                HorizontalDivider()

                Text("Ingredientes:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text(recetaSeleccionada.ingredientes, style = MaterialTheme.typography.bodyLarge)

                HorizontalDivider()

                Text("Instrucciones paso a paso:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text(recetaSeleccionada.instrucciones, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}