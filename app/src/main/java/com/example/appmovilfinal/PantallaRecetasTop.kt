package com.example.appmovilfinal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRecetasTop(navController: NavController, viewModel: RecetaViewModel) {
    // tres estados que configuramos en el ViewModel
    val listaRecetas by viewModel.recetasTop.collectAsState()
    val estaCargando by viewModel.estadoCarga.collectAsState()
    val error by viewModel.errorApi.collectAsState()

    // cargamos las recetas
    LaunchedEffect(Unit) {
        viewModel.cargarRecetasTop()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recetas Top Globales") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                // Estado 1: Está descargando los datos de internet
                estaCargando -> {
                    CircularProgressIndicator()
                }
                // Estado 2: Hubo un fallo en la conexión
                error != null -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Sin conexión",
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.error
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "¡Sin conexión a internet!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = error ?: "No se pudo conectar al servidor.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { viewModel.cargarRecetasTop() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reintentar",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Reintentar conexión", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                // Estado 3: La información se descargó con éxito
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(listaRecetas) { recetaRemota ->
                            ItemRecetaTop(recetaRemota)
                        }
                    }
                }
            }
        }
    }
}

// El diseño visual para las recetas que vienen de la API
@Composable
fun ItemRecetaTop(receta: RecetaRemota) {
    // Variable de estado que recuerda si la tarjeta está expandida o no
    var expandido by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expandido = !expandido }, // Al hacer clic, invierte el estado (de cerrado a abierto y viceversa)
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column {
            AsyncImage(
                model = receta.image,
                contentDescription = receta.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = receta.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "⏱️ ${receta.prepTimeMinutes} min", color = MaterialTheme.colorScheme.primary)

                // DETALLES EXPANDIBLES
                if (expandido) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Ingredientes:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    // La API devuelve una lista
                    Text(text = receta.ingredients.joinToString(", "), style = MaterialTheme.typography.bodyMedium)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "Instrucciones:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    // Unimos los pasos de la API con viñetas para que se lea ordenado
                    Text(text = receta.instructions.joinToString("\n• ", prefix = "• "), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}