package com.example.appmovilfinal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// 1. El ViewModel que prepara los datos para la interfaz
class RecetaViewModel(private val repository: RecetaRepository) : ViewModel() {

    val recetas: StateFlow<List<Receta>> = repository.todasLasRecetas
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun agregarReceta(nombrePlatillo: String, ingredientes: String, tiempoPreparacion: Int, instrucciones: String) {
        viewModelScope.launch {
            val nuevaReceta = Receta(
                nombrePlatillo = nombrePlatillo,
                ingredientes = ingredientes,
                tiempoPreparacion = tiempoPreparacion,
                instrucciones = instrucciones
            )
            repository.insertar(nuevaReceta)
        }
    }
}

// 2. El Fabricante (Factory) para enseñar a Android a construir este ViewModel
class RecetaViewModelFactory(private val repository: RecetaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecetaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecetaViewModel(repository) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}