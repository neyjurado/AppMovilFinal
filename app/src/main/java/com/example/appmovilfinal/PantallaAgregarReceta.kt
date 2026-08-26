package com.example.appmovilfinal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAgregarReceta(navController: NavController, viewModel: RecetaViewModel) {
    val context = LocalContext.current

    // Variables de texto
    var nombre by remember { mutableStateOf("") }
    var ingredientes by remember { mutableStateOf("") }
    var tiempo by remember { mutableStateOf("") }
    var instrucciones by remember { mutableStateOf("") }

    // Variables para la cámara
    var uriTemporal by remember { mutableStateOf<Uri?>(null) }
    var imagenGuardada by remember { mutableStateOf<String?>(null) }

    // El "Contrato" de la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()//no tendra previsualisacion como con take picturepreview
    ) { exito ->
        if (exito) {
            imagenGuardada = uriTemporal.toString()
        }
    }

    // LANZADOR DE PERMISOS
    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Si acepta, disparamos la cámara inmediatamente
            val uri = crearUriParaFoto(context)
            uriTemporal = uri
            cameraLauncher.launch(uri)
        } else {
            //  Manejo del caso en que el usuario lo rechace
            Toast.makeText(context, "Permiso denegado. No se puede tomar foto.", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Receta") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre del platillo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = ingredientes, onValueChange = { ingredientes = it }, label = { Text("Ingredientes") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = tiempo, onValueChange = { tiempo = it }, label = { Text("Tiempo (minutos)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = instrucciones, onValueChange = { instrucciones = it }, label = { Text("Instrucciones") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

            // BOTÓN DE CÁMARA
            Button(
                onClick = {
                    // Verificamos si ya tenemos el permiso
                    val permisoConcedido = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (permisoConcedido) {
                        // Ya tenemos permiso, abrimos cámara directo
                        val uri = crearUriParaFoto(context)
                        uriTemporal = uri
                        cameraLauncher.launch(uri)
                    } else {
                        // CUMPLE REQUISITO: Solicitud de permisos en tiempo de ejecución
                        permisoLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(if (imagenGuardada == null) "📸 Tomar Foto del Platillo" else "✅ ¡Foto Capturada!")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (nombre.isNotBlank() && tiempo.isNotBlank()) {
                        viewModel.agregarReceta(
                            nombrePlatillo = nombre,
                            ingredientes = ingredientes,
                            tiempoPreparacion = tiempo.toIntOrNull() ?: 0,
                            instrucciones = instrucciones,
                            imagenUri = imagenGuardada
                        )
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

fun crearUriParaFoto(context: Context): Uri {
    val file = File(context.cacheDir, "foto_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, context.packageName + ".provider", file)
}