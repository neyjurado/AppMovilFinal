package com.example.appmovilfinal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAgregarReceta(navController: NavController, viewModel: RecetaViewModel) {
    // 1. Variables de estado para recordar lo que el usuario escribe
    var nombre by remember { mutableStateOf("") }
    var ingredientes by remember { mutableStateOf("") }
    var tiempo by remember { mutableStateOf("") }
    var instrucciones by remember { mutableStateOf("") }

    // 2. El lienzo de la pantalla
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Receta") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        // 3. Columna para organizar los campos de texto
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del platillo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = ingredientes,
                onValueChange = { ingredientes = it },
                label = { Text("Ingredientes (separados por coma)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tiempo,
                onValueChange = { tiempo = it },
                label = { Text("Tiempo de preparación (minutos)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), // Muestra teclado numérico
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = instrucciones,
                onValueChange = { instrucciones = it },
                label = { Text("Instrucciones paso a paso") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3 // Hace la caja de texto más grande por defecto
            )

            Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia abajo

            // 4. Botón de Guardar
            Button(
                onClick = {
                    if (nombre.isNotBlank() && tiempo.isNotBlank()) {
                        // Enviamos los datos al ViewModel para guardarlos
                        viewModel.agregarReceta(
                            nombrePlatillo = nombre,
                            ingredientes = ingredientes,
                            tiempoPreparacion = tiempo.toIntOrNull() ?: 0,
                            instrucciones = instrucciones
                        )
                        // Regresamos a la pantalla anterior
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Guardar Receta", fontSize = MaterialTheme.typography.titleMedium.fontSize)
            }
        }
    }
}
