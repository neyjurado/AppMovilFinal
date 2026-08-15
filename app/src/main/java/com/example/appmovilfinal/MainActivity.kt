package com.example.appmovilfinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
            // 1. Leemos el DataStore al nivel más alto de la app para saber si activar el modo oscuro
            val context = LocalContext.current
            val ajustesDataStore = remember { AjustesDataStore(context) }
            val modoOscuroActivado by ajustesDataStore.modoOscuroFlow.collectAsState(initial = false)

            // 2. Evaluamos qué paleta de colores usar
            val colores = if (modoOscuroActivado) darkColorScheme() else lightColorScheme()

            // 3. Aplicamos el tema dinámico a toda la aplicación
            MaterialTheme(colorScheme = colores) {
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

        composable(
            route = "detalle/{recetaId}",
            arguments = listOf(androidx.navigation.navArgument("recetaId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("recetaId") ?: 0
            PantallaDetalleReceta(navController, viewModel, id)
        }

        composable("recetas_top") {
            PantallaRecetasTop(navController = navController, viewModel = viewModel)
        }

        // La nueva ruta para tus preferencias
        composable("ajustes") {
            PantallaAjustes(navController = navController)
        }

    }
}