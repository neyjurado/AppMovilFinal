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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Conectar la lógica con la interfaz gráfica
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                NavegacionPrincipal()
            }
        }
    }
}

@Composable
fun NavegacionPrincipal() {
    // 2. El motor de navegación
    val navController = rememberNavController()

    // 3. El mapa de rutas de la aplicación
    NavHost(navController = navController, startDestination = "inicio") {

        composable("inicio") { PantallaInicio(navController) }
        composable("catalogo") { PantallaCatalogo(navController) }
        composable("agregar") { PantallaAgregarReceta(navController) }
        composable("detalle") { PantallaDetalleReceta(navController) }
        composable("ajustes") { PantallaAjustes(navController) }

    }
}

// --- 4. LAS 5 PANTALLAS (CASCARONES VACÍOS) ---

@Composable
fun PantallaInicio(navController: NavController) {
    Text("1. Pantalla de Inicio (Dashboard)")
}

@Composable
fun PantallaCatalogo(navController: NavController) {
    Text("2. Catálogo de Recetas")
}

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