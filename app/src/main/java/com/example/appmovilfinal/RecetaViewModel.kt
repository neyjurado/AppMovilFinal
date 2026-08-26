package com.example.appmovilfinal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

class RecetaViewModel(private val repository: RecetaRepository) : ViewModel() {

    // DATOS LOCALES (Base de datos Room
    val recetas: StateFlow<List<Receta>> = repository.todasLasRecetas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun agregarReceta(
        nombrePlatillo: String,
        ingredientes: String,
        tiempoPreparacion: Int,
        instrucciones: String,
        imagenUri: String? = null
    ) {
        viewModelScope.launch {
            val nuevaReceta = Receta(
                nombrePlatillo = nombrePlatillo,
                ingredientes = ingredientes,
                tiempoPreparacion = tiempoPreparacion,
                instrucciones = instrucciones,
                imagenUri = imagenUri
            )
            repository.insertar(nuevaReceta)
        }
    }

    //FUNCIÓN PARA BORRAR
    fun borrarReceta(receta: Receta) {
        viewModelScope.launch {
            repository.borrar(receta)
        }
    }

    //DATOS REMOTOS (API de internet con Retrofit)
    private val _recetasTop = MutableStateFlow<List<RecetaRemota>>(emptyList())
    val recetasTop: StateFlow<List<RecetaRemota>> = _recetasTop

    private val _estadoCarga = MutableStateFlow(false)
    val estadoCarga: StateFlow<Boolean> = _estadoCarga

    private val _errorApi = MutableStateFlow<String?>(null)
    val errorApi: StateFlow<String?> = _errorApi

    fun cargarRecetasTop() {
        viewModelScope.launch {
            _estadoCarga.value = true
            _errorApi.value = null
            try {
                // Va a internet y espera el resultado
                val resultado = repository.obtenerRecetasTopRemotas()
                _recetasTop.value = resultado
                _estadoCarga.value = false
            } catch (e: IOException) {
                // Error de conexión de red (sin internet, timeout, DNS)
                _errorApi.value = "No se pudo conectar a internet. Por favor, verifica tu conexión (Wi-Fi o datos móviles) para explorar las recetas globales."
                _estadoCarga.value = false
            } catch (e: Exception) {
                // Otro tipo de fallo del servidor o inesperado
                _errorApi.value = "Ocurrió un inconveniente al consultar las recetas del servidor. Por favor, inténtalo de nuevo más tarde."
                _estadoCarga.value = false
            }
        }
    }
}

// El Fabricante (Factory) para crear este ViewModel con dependencias
class RecetaViewModelFactory(private val repository: RecetaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecetaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecetaViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}