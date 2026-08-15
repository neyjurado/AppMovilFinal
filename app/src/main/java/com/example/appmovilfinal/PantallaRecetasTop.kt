package com.example.appmovilfinal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRecetasTop(navController: NavController, viewModel: RecetaViewModel) {
    // Escuchamos los tres estados que configuramos en el ViewModel
    val listaRecetas by viewModel.recetasTop.collectAsState()
    val estaCargando by viewModel.estadoCarga.collectAsState()
    val error by viewModel.errorApi.collectAsState()

    // Este bloque ejecuta la función de internet la primera vez que se entra a la pantalla
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
                    Text(
                        text = error ?: "Ocurrió un error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
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

                // --- NUEVA SECCIÓN DE DETALLES EXPANDIBLES ---
                if (expandido) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = "Ingredientes:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    // La API devuelve una lista, así que la unimos con comas
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