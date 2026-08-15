package com.example.appmovilfinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavegacionPrincipal()
                }
            }
        }
    }
}

@Composable
fun NavegacionPrincipal() {
    // 1. Obtenemos el contexto actual de la aplicación
    val context = LocalContext.current

    // 2. Instanciamos la Base de Datos y el Repositorio
    val database = RecetaDatabase.getDatabase(context)
    val repository = RecetaRepository(database.recetaDao())

    // 3. Creamos el ViewModel usando nuestra fábrica (Factory)
    val viewModel: RecetaViewModel = viewModel(
        factory = RecetaViewModelFactory(repository)
    )

    // 4. El motor de navegación
    val navController = rememberNavController()

    // 5. El mapa de rutas de la aplicación
    NavHost(navController = navController, startDestination = "inicio") {

        composable("inicio") { PantallaInicio(navController, viewModel) }
        composable("catalogo") { PantallaCatalogo(navController, viewModel) }
        composable("agregar") { PantallaAgregarReceta(navController, viewModel) }
        composable("detalle") { PantallaDetalleReceta(navController) }
        composable("ajustes") { PantallaAjustes(navController) }

    }
}

//// --- LAS 5 PANTALLAS (Cascarones vacíos temporalmente, excepto Inicio y Catálogo que ya tienen su propio archivo) ---

@Composable
fun PantallaAgregarReceta(navController: NavController) {
    Text("3. Formulario para Agregar Receta")
}

@Composable
fun PantallaDetalleReceta(navController: NavController) {
    Text("4. Detalles de la Receta")
}

@Composable
fun PantallaAjustes(navController: NavController) {
    Text("5. Ajustes y Favoritos")
}