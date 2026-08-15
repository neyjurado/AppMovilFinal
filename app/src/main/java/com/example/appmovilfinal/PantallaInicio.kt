package com.example.appmovilfinal

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaInicio(navController: NavController, viewModel: RecetaViewModel) {
    // 1. Observamos la base de datos para saber cuántas recetas hay en total
    val listaRecetas by viewModel.recetas.collectAsState()

    // 2. Scaffold: El lienzo base con la barra superior
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inicio", fontWeight = FontWeight.Bold) },
                actions = {
                    // --- NUEVO: Botón de Ajustes ---
                    IconButton(onClick = { navController.navigate("ajustes") }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Ir a Ajustes",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        // 3. Columna central para alinear todo al medio
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¡Bienvenido a tu Cocina!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Tarjeta de resumen
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total de recetas guardadas:", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${listaRecetas.size}", // Muestra el número real de recetas
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 5. Botón principal de navegación
            Button(
                onClick = { navController.navigate("catalogo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Ir a mi Catálogo", fontSize = MaterialTheme.typography.titleMedium.fontSize)
            }
        }
    }
}