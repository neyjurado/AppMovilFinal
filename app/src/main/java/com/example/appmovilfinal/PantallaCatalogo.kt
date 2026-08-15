package com.example.appmovilfinal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCatalogo(navController: NavController, viewModel: RecetaViewModel) {
    // 1. Conectamos la interfaz a la base de datos de forma reactiva
    val listaRecetas by viewModel.recetas.collectAsState()

    // 2. Scaffold: El esqueleto visual de la pantalla (Barra superior y botón flotante)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Recetas", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("agregar") }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar nueva receta")
            }
        }
    ) { paddingValues ->
        // 3. LazyColumn: La lista deslizable (scroll)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp), // Espacio a los lados para que no toque los bordes
            verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre cada tarjeta
        ) {
            // Iteramos sobre nuestra lista de la base de datos
            items(listaRecetas) { receta ->
                ItemReceta(receta = receta) {
                    // Acción al hacer clic en una receta
                    navController.navigate("detalle")
                }
            }
        }
    }
}

// 4. El diseño individual de cada "Tarjeta" de receta
@Composable
fun ItemReceta(receta: Receta, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Le da una sombra atractiva
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = receta.nombrePlatillo,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ingredientes: ${receta.ingredientes}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2 // Si hay muchos ingredientes, corta el texto con "..."
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "⏱️ ${receta.tiempoPreparacion} min",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
