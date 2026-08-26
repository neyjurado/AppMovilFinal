package com.example.appmovilfinal

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAjustes(navController: NavController) {
    val context = LocalContext.current
    // Instanciamos nuestra clase administradora de DataStore
    val dataStore = remember { AjustesDataStore(context) }

    // lanzar corrutinas desde la interfaz visual
    val coroutineScope = rememberCoroutineScope()

    // Leemos el valor guardado en el celular de forma reactiva
    val modoOscuroActivado by dataStore.modoOscuroFlow.collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes de la App") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Preferencias Visuales",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Activar Modo Oscuro", style = MaterialTheme.typography.bodyLarge)

                // El interruptor (Switch)
                Switch(
                    checked = modoOscuroActivado,
                    onCheckedChange = { activado ->
                        // corrutina con launch
                        coroutineScope.launch {
                            dataStore.guardarModoOscuro(activado)
                        }
                    }
                )
            }
        }
    }
}