package com.example.appmovilfinal

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 1. Instancia global del DataStore (se asocia al contexto de la app para no crear múltiples archivos)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ajustes_usuario")

class AjustesDataStore(private val context: Context) {

    // 2. Definimos la "llave" específica para guardar nuestro valor (como si fuera una etiqueta de un cajón)
    companion object {
        val MODO_OSCURO_KEY = booleanPreferencesKey("modo_oscuro_activado")
    }

    // 3. Función reactiva para LEER el estado actual usando Flow
    val modoOscuroFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        // Si no hay nada guardado todavía, por defecto devolvemos 'false' (modo claro)
        preferences[MODO_OSCURO_KEY] ?: false
    }

    // 4. Función asíncrona para GUARDAR el nuevo estado
    suspend fun guardarModoOscuro(activado: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[MODO_OSCURO_KEY] = activado
        }
    }
}
