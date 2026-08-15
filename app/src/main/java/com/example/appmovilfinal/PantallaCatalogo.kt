package com.example.appmovilfinal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
        // 3. Contenedor principal en columna para acomodar el botón y la lista
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- NUEVO: Botón de Recetas Top para consumir la API ---
            Button(
                onClick = { navController.navigate("recetas_top") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("🌟 Explorar Recetas Top (API)", fontWeight = FontWeight.Bold)
            }

            // 4. LazyColumn: La lista deslizable (scroll) de recetas locales
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp), // Espacio a los lados para que no toque los bordes
                verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre cada tarjeta
            ) {
                // Iteramos sobre nuestra lista de la base de datos local
                items(listaRecetas) { receta ->
                    ItemReceta(
                        receta = receta,
                        navController = navController,
                        onClick = {}
                    )
                }
            }
        }
    }
}

// 5. El diseño individual de cada "Tarjeta" de receta
@Composable
fun ItemReceta(receta: Receta, navController: NavController, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("detalle/${receta.id}") },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Le da una sombra atractiva
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            // --- Cargador de Imagen con Coil ---
            if (receta.imagenUri != null) {
                AsyncImage(
                    model = receta.imagenUri,
                    contentDescription = "Foto de ${receta.nombrePlatillo}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp) // Altura de la foto
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop // Recorta la imagen para que llene el espacio sin deformarse
                )
            }

            // Textos originales
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
}