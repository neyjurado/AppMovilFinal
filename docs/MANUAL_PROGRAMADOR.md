# Manual del Programador — Mis Recetas App

## 1. Introducción y Descripción General

El **Manual del Programador** tiene como propósito servir de guía exhaustiva para cualquier desarrollador que desee entender, mantener, modificar o extender la aplicación Android **"Mis Recetas"**. Este documento abarca la arquitectura, el flujo de datos, la estructura del proyecto y guías paso a paso para realizar las modificaciones más comunes.

**Descripción de la app:**
"Mis Recetas" es una aplicación móvil nativa para Android que permite a los usuarios gestionar su propio recetario. Las funcionalidades clave incluyen guardar recetas localmente (con fotos desde la cámara), consultar un catálogo de recetas guardadas, ver el detalle de cada una, consumir un top de recetas desde una API REST externa y configurar el tema visual de la aplicación (Modo Claro/Oscuro) mediante preferencias persistentes.

**Tecnologías usadas:**

| Tecnología | Descripción / Propósito |
| :--- | :--- |
| **Kotlin** | Lenguaje principal de desarrollo (versión 2.2.10). |
| **Jetpack Compose** | Toolkit moderno para la construcción de la Interfaz de Usuario (UI) de forma declarativa. |
| **Room Database** | Abstracción de SQLite para el almacenamiento local y persistencia de recetas. |
| **Retrofit + Gson** | Cliente HTTP para consumir la API REST externa y serialización de JSON. |
| **DataStore Preferences** | Almacenamiento asíncrono y persistente de preferencias de usuario (ej. Modo Oscuro). |
| **Coil** | Biblioteca moderna y rápida para la carga y caché de imágenes en Jetpack Compose. |
| **Navigation Compose** | Gestión del enrutamiento y navegación entre pantallas. |
| **Coroutines & Flow** | Gestión de concurrencia, asincronía y flujos de datos reactivos. |

**Autor:** Ney Jurado

---

## 2. Requisitos del Entorno de Desarrollo

Para trabajar en este proyecto, tu entorno debe cumplir con los siguientes requisitos:

*   **IDE:** Android Studio (Versión recomendada: Ladybug o superior).
*   **Java Development Kit (JDK):** JDK 17.
*   **Kotlin:** Versión 2.2.10.
*   **Android Gradle Plugin (AGP):** Versión 9.2.1.
*   **Niveles de SDK:**
    *   `compileSdk`: 37
    *   `minSdk`: 29 (Android 10)
    *   `targetSdk`: 37

> [!IMPORTANT]  
> Asegúrate de configurar la versión del JDK a la 17 en la configuración del proyecto de Android Studio (`File > Project Structure > SDK Location > Gradle Settings`).

**Cómo clonar y abrir el proyecto:**
1. Clona el repositorio: `git clone <url-del-repositorio>`
2. Abre Android Studio y selecciona `File > Open...`.
3. Navega hasta la carpeta del proyecto clonado y selecciónala.
4. Espera a que Gradle sincronice las dependencias.

---

## 3. Estructura del Proyecto

El proyecto sigue una estructura de directorios estándar de Android adaptada para Jetpack Compose y MVVM.

```text
app/src/main/
├── java/com/example/appmovilfinal/
│   ├── MainActivity.kt                # Punto de entrada y configuración de navegación
│   ├── Receta.kt                      # Entidad Room (Modelo de datos local)
│   ├── RecetaDao.kt                   # Interfaz de acceso a datos locales
│   ├── RecetaDatabase.kt              # Configuración de base de datos Room
│   ├── RecetasApiService.kt           # Interfaces y cliente Retrofit
│   ├── RecetaRemota.kt                # DTOs para la API
│   ├── AjustesDataStore.kt            # Gestión de preferencias locales
│   ├── RecetaRepository.kt            # Repositorio (Single Source of Truth)
│   └── RecetaViewModel.kt             # Lógica de presentación y gestión de estado
├── res/
│   ├── xml/file_paths.xml             # Configuración del FileProvider para la cámara
│   ├── values/                        # Recursos de strings, colors, themes
│   └── ...
└── AndroidManifest.xml                # Declaración de permisos, aplicación y componentes
```

**Tabla de responsabilidad de cada archivo:**

| Archivo | Descripción | Capa |
| :--- | :--- | :--- |
| `Receta.kt` | Data class que representa una receta. Anotada con `@Entity` para Room. | Data (Local) |
| `RecetaDao.kt` | Interfaz con métodos abstractos (`@Insert`, `@Query`) para acceder a la BD. | Data (Local) |
| `RecetaDatabase.kt` | Clase base abstracta de Room que expone el DAO y crea la instancia de la BD. | Data (Local) |
| `RecetasApiService.kt` | Definición de endpoints HTTP y creación del cliente Retrofit. | Data (Remota) |
| `RecetaRemota.kt` | DTOs (Data Transfer Objects) que mapean la respuesta JSON de la API. | Data (Remota) |
| `AjustesDataStore.kt` | Manejo de preferencias usando Preferences DataStore. | Data (Local) |
| `RecetaRepository.kt` | Mediador entre los Data Sources (Room, Retrofit) y el ViewModel. | Domain/Data |
| `RecetaViewModel.kt` | Gestiona el estado de la UI y la lógica de negocio, exponiendo `StateFlow`. | UI/Presentation |

---

## 4. Arquitectura MVVM + Patrón Repository

La aplicación sigue el patrón arquitectónico **MVVM (Model-View-ViewModel)** combinado con el patrón **Repository**. Esto asegura la separación de responsabilidades y facilita el testeo y mantenimiento.

> [!NOTE]  
> Referencia visual: Revisa el diagrama en `diagramas/arquitectura_mvvm.drawio` o la captura `capturas/07_diagrama_arquitectura.png` para entender el flujo.

### Explicación de las Capas

1.  **Capa de Interfaz de Usuario (UI / View):** Compuesta por las funciones `@Composable` en Jetpack Compose. Observa pasivamente los estados emitidos por el ViewModel y envía eventos de usuario (clics, ingresos de texto) al ViewModel.
2.  **Capa de Presentación (ViewModel):** `RecetaViewModel` contiene la lógica de negocio orientada a la UI. Transforma los datos del Repositorio en estados (`StateFlow`) que la UI puede consumir. No sabe nada de la procedencia de los datos (BD o API).
3.  **Capa de Datos (Repository):** `RecetaRepository` es la fuente única de verdad (*Single Source of Truth*). Decide si obtener datos localmente mediante Room o remotamente a través de Retrofit.
4.  **Capa de Fuentes de Datos (Data Sources):** Room (Base de Datos Local), Retrofit (API REST), DataStore (Preferencias).

### Flujo de Datos
`UI (Compose) ↔ ViewModel ↔ Repository ↔ Data Sources (Room / Retrofit)`

### Inyección de Dependencias Manual
En lugar de usar bibliotecas pesadas como Hilt o Dagger, el proyecto utiliza inyección de dependencias manual. Esto reduce la complejidad en aplicaciones de tamaño pequeño a mediano.

**Código de la inyección manual:**
En `MainActivity.kt` o en el componente de navegación principal (`NavegacionPrincipal`), se instancian las dependencias de abajo hacia arriba:

```kotlin
// 1. Instanciar Base de Datos
val database = RecetaDatabase.getDatabase(context)

// 2. Instanciar Repositorio pasando el DAO
val repository = RecetaRepository(database.recetaDao())

// 3. Crear Factory para el ViewModel
val factory = RecetaViewModelFactory(repository)

// 4. Obtener ViewModel
val viewModel: RecetaViewModel = viewModel(factory = factory)
```
La clase `RecetaViewModelFactory` se encarga de crear el ViewModel pasándole el repositorio requerido.

---

## 5. Base de Datos Room — Guía Completa

Room provee una capa de abstracción sobre SQLite. En esta aplicación se utiliza para persistir las recetas creadas por el usuario.

### 5.1 Entidad: Receta.kt

```kotlin
package com.example.appmovilfinal
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recetas")
data class Receta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombrePlatillo: String,
    val ingredientes: String,
    val tiempoPreparacion: Int,
    val instrucciones: String,
    val imagenUri: String? = null
)
```
**Explicación:**
*   `@Entity(tableName = "recetas")`: Indica a Room que esta data class representa una tabla en SQLite llamada "recetas".
*   `@PrimaryKey(autoGenerate = true)`: Define la columna `id` como clave primaria autoincremental.

**Tabla de campos:**

| Campo | Tipo | Descripción |
| :--- | :--- | :--- |
| `id` | Int | Identificador único autogenerado. |
| `nombrePlatillo` | String | Nombre de la receta. |
| `ingredientes` | String | Lista de ingredientes (como cadena de texto). |
| `tiempoPreparacion` | Int | Tiempo estimado en minutos. |
| `instrucciones` | String | Pasos de preparación. |
| `imagenUri` | String? | Ruta local de la foto tomada con la cámara (puede ser nulo). |

### 5.2 DAO: RecetaDao.kt

```kotlin
package com.example.appmovilfinal
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecetaDao {
    @Insert
    suspend fun insertarReceta(receta: Receta)

    @Query("SELECT * FROM recetas ORDER BY id DESC")
    fun obtenerTodasLasRecetas(): Flow<List<Receta>>

    @Delete
    suspend fun borrarReceta(receta: Receta)
}
```
**Explicación:**
*   `@Dao`: Marca la interfaz como Data Access Object.
*   `@Insert` y `@Delete`: Generan automáticamente las sentencias SQL de inserción y borrado. Son funciones `suspend` porque realizan operaciones de E/S y deben ejecutarse en una corrutina (fuera del hilo principal).
*   `@Query`: Permite escribir consultas SQL personalizadas. Retorna un `Flow<List<Receta>>`, lo que significa que la base de datos notificará automáticamente y emitirá una nueva lista cada vez que la tabla "recetas" cambie. No es `suspend` porque Flow ya maneja la asincronía.

### 5.3 Database: RecetaDatabase.kt

```kotlin
package com.example.appmovilfinal
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Receta::class], version = 2, exportSchema = false)
abstract class RecetaDatabase : RoomDatabase() {
    abstract fun recetaDao(): RecetaDao
    
    companion object {
        @Volatile
        private var INSTANCE: RecetaDatabase? = null
        
        fun getDatabase(context: Context): RecetaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecetaDatabase::class.java,
                    "receta_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```
**Explicación:**
*   `@Database`: Define las entidades y la versión de la base de datos.
*   **Patrón Singleton:** El bloque `companion object` asegura que solo exista una instancia de la base de datos en toda la aplicación.
*   `@Volatile`: Garantiza que el valor de `INSTANCE` sea siempre visible para todos los hilos de ejecución inmediatamente.
*   `synchronized(this)`: Previene que múltiples hilos creen múltiples instancias al mismo tiempo.
*   `fallbackToDestructiveMigration()`: Si se incrementa la versión y no se provee una estrategia de migración, Room eliminará y recreará las tablas (borrando los datos). Útil en desarrollo, peligroso en producción.

### 5.4 🔧 Cómo Modificar la Base de Datos

#### Caso 1: Agregar un nuevo campo a Receta

**Paso a paso:**
1. Modificar la data class `Receta`.
2. Incrementar el número de `version` en `RecetaDatabase`.

**ANTES:**
```kotlin
// Receta.kt
data class Receta(
    // ...
    val instrucciones: String,
    val imagenUri: String? = null
)

// RecetaDatabase.kt
@Database(entities = [Receta::class], version = 2, exportSchema = false)
```

**DESPUÉS (Ej. agregando `categoria`):**
```kotlin
// Receta.kt
data class Receta(
    // ...
    val instrucciones: String,
    val imagenUri: String? = null,
    val categoria: String = "General" // Proveer valor por defecto ayuda a evitar crashes en apps existentes si se permite null
)

// RecetaDatabase.kt
@Database(entities = [Receta::class], version = 3, exportSchema = false)
```

> [!WARNING]  
> Al usar `fallbackToDestructiveMigration()`, los usuarios perderán sus recetas al actualizar la app. Ver Caso 4 para migraciones seguras.

#### Caso 2: Agregar una nueva consulta al DAO

Si necesitas buscar recetas por nombre, modifica el DAO:

```kotlin
// RecetaDao.kt
@Dao
interface RecetaDao {
    // ... (código existente) ...

    @Query("SELECT * FROM recetas WHERE nombrePlatillo LIKE '%' || :busqueda || '%'")
    fun buscarRecetaPorNombre(busqueda: String): Flow<List<Receta>>
}
```

#### Caso 3: Agregar una nueva entidad

Imagina que quieres guardar "Categorías".
1. Crea la entidad `Categoria.kt`:
```kotlin
@Entity(tableName = "categorias")
data class Categoria(@PrimaryKey(autoGenerate = true) val id: Int, val nombre: String)
```
2. Crea el `CategoriaDao.kt`.
3. Actualiza `RecetaDatabase.kt`:
```kotlin
@Database(entities = [Receta::class, Categoria::class], version = 3, exportSchema = false) // Añadir a entities
abstract class RecetaDatabase : RoomDatabase() {
    abstract fun recetaDao(): RecetaDao
    abstract fun categoriaDao(): CategoriaDao // Nueva función
    // ...
}
```

#### Caso 4: Migración sin pérdida de datos

Si quieres agregar un campo sin perder datos en producción, debes definir una migración.

```kotlin
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Definir la migración de versión 2 a 3
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE recetas ADD COLUMN categoria TEXT NOT NULL DEFAULT 'General'")
    }
}

// En RecetaDatabase.kt:
Room.databaseBuilder(
    context.applicationContext,
    RecetaDatabase::class.java,
    "receta_database"
)
    .addMigrations(MIGRATION_2_3) // Agregar esta línea
    // .fallbackToDestructiveMigration() // Opcionalmente quitar esto
    .build()
```

---

## 6. API REST con Retrofit — Guía Completa

Retrofit es la biblioteca estándar para consumir APIs HTTP en Android. Aquí consumimos un endpoint de `dummyjson.com`.

### 6.1 Interface del Servicio: RecetasApiService.kt

```kotlin
package com.example.appmovilfinal
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface RecetasApiService {
    @GET("recipes")
    suspend fun obtenerRecetasTop(): RespuestaRecetasApi
}
```
**Explicación:**
*   `@GET("recipes")`: Define que esta función realizará una petición HTTP GET al path `/recipes`.
*   `suspend fun`: La petición se ejecuta de forma asíncrona dentro de una corrutina.

### 6.2 Cliente Retrofit: RetrofitClient

```kotlin
object RetrofitClient {
    val api: RecetasApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecetasApiService::class.java)
    }
}
```
**Explicación:**
*   `baseUrl`: URL base de la API. Debe terminar con una barra diagonal `/`.
*   `addConverterFactory(GsonConverterFactory.create())`: Configura a Gson para que automáticamente transforme la respuesta JSON en objetos Kotlin (DTOs).
*   `by lazy`: Inicializa la instancia de Retrofit solo la primera vez que se accede a `RetrofitClient.api`.

### 6.3 DTOs: RecetaRemota.kt

```kotlin
package com.example.appmovilfinal

data class RespuestaRecetasApi(
    val recipes: List<RecetaRemota>
)

data class RecetaRemota(
    val id: Int,
    val name: String,
    val ingredients: List<String>,
    val prepTimeMinutes: Int,
    val instructions: List<String>,
    val image: String
)
```
**Explicación:**
*   Estas clases modelan exactamente la estructura JSON devuelta por la API. Si el JSON tiene un array llamado "recipes", la variable en Kotlin debe llamarse `recipes`. Si necesitas nombres distintos, usarías la anotación `@SerializedName("nombre_json")`.

### 6.4 🔧 Cómo Modificar la API

#### Caso 1: Cambiar la URL base
Modifica `RetrofitClient` en `RecetasApiService.kt`.

**ANTES:**
```kotlin
.baseUrl("https://dummyjson.com/")
```
**DESPUÉS:**
```kotlin
.baseUrl("https://mi-nueva-api.com/api/v1/")
```

#### Caso 2: Agregar un nuevo endpoint
Agrega la función a `RecetasApiService`.

**ANTES:**
```kotlin
interface RecetasApiService {
    @GET("recipes")
    suspend fun obtenerRecetasTop(): RespuestaRecetasApi
}
```
**DESPUÉS (Ej. obtener receta por ID):**
```kotlin
import retrofit2.http.Path

interface RecetasApiService {
    @GET("recipes")
    suspend fun obtenerRecetasTop(): RespuestaRecetasApi

    @GET("recipes/{id}")
    suspend fun obtenerRecetaPorId(@Path("id") recetaId: Int): RecetaRemota
}
```

#### Caso 3: Consumir una API completamente diferente
1. Cambia la `baseUrl` en `RetrofitClient`.
2. Borra o modifica `RecetasApiService`.
3. Borra `RecetaRemota.kt` y crea nuevas Data Classes usando un plugin como JSON to Kotlin Class para mapear los nuevos JSONs exactos.
4. Actualiza `RecetaRepository` y `RecetaViewModel` para usar los nuevos métodos y tipos de datos.

#### Caso 4: Agregar headers o interceptores
Para agregar un header (ej. Token de Autorización), debes configurar un `OkHttpClient` y pasárselo a Retrofit.

**DESPUÉS:**
```kotlin
import okhttp3.OkHttpClient

object RetrofitClient {
    private val httpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer MI_TOKEN_SECRETO")
            .build()
        chain.proceed(request)
    }.build()

    val api: RecetasApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .client(httpClient) // <- Asignar el cliente aquí
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecetasApiService::class.java)
    }
}
```

#### Caso 5: Agregar timeout y logging
Usando el mismo `OkHttpClient.Builder`:

```kotlin
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
val httpClient = OkHttpClient.Builder()
    .addInterceptor(logging) // Imprime las peticiones en el Logcat
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build()
```

---

## 7. DataStore de Preferencias — Guía Completa

DataStore Preferences reemplaza a SharedPreferences, siendo asíncrono y seguro para tipos gracias a Flow. En esta app gestiona el Modo Oscuro.

### 7.1 Implementación actual: AjustesDataStore.kt

```kotlin
package com.example.appmovilfinal
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ajustes_usuario")

class AjustesDataStore(private val context: Context) {
    companion object {
        val MODO_OSCURO_KEY = booleanPreferencesKey("modo_oscuro_activado")
    }
    
    val modoOscuroFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[MODO_OSCURO_KEY] ?: false
    }
    
    suspend fun guardarModoOscuro(activado: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[MODO_OSCURO_KEY] = activado
        }
    }
}
```
**Explicación:**
*   `preferencesDataStore`: Un delegado que crea el DataStore. Al ser un nivel superior al Context, asegura que sea un Singleton.
*   `booleanPreferencesKey`: Define una clave tipada.
*   `context.dataStore.data.map`: Observa los cambios reactivamente.
*   `context.dataStore.edit`: Función de suspensión para escribir datos de forma transaccional.

### 7.2 🔧 Cómo Modificar DataStore

#### Caso 1: Agregar una nueva preferencia Boolean
(Ej. notificacionesActivadas)

```kotlin
class AjustesDataStore(private val context: Context) {
    companion object {
        val MODO_OSCURO_KEY = booleanPreferencesKey("modo_oscuro_activado")
        val NOTIFICACIONES_KEY = booleanPreferencesKey("notificaciones_activadas") // 1. Nueva clave
    }
    
    // ... código existente ...

    // 2. Nuevo Flow de lectura
    val notificacionesFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICACIONES_KEY] ?: true // valor por defecto: true
    }

    // 3. Nueva función de guardado
    suspend fun guardarNotificaciones(activadas: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICACIONES_KEY] = activadas
        }
    }
}
```

#### Caso 2: Agregar una preferencia String
(Ej. idioma del usuario)

```kotlin
import androidx.datastore.preferences.core.stringPreferencesKey

val IDIOMA_KEY = stringPreferencesKey("idioma_usuario")

val idiomaFlow: Flow<String> = context.dataStore.data.map { preferences ->
    preferences[IDIOMA_KEY] ?: "es"
}

suspend fun guardarIdioma(idioma: String) {
    context.dataStore.edit { preferences -> preferences[IDIOMA_KEY] = idioma }
}
```

#### Caso 3: Agregar una preferencia Int
(Ej. tamaño de fuente)

```kotlin
import androidx.datastore.preferences.core.intPreferencesKey

val TAMANO_FUENTE_KEY = intPreferencesKey("tamano_fuente")
// Similar a los anteriores, con valor por defecto ej. 14.
```

#### Caso 4: Leer la preferencia en otra pantalla
Para consumir el DataStore en una UI en Compose, pásalo al ViewModel, o si estás en el punto de entrada:

```kotlin
val ajustesDataStore = AjustesDataStore(context)
// En un @Composable:
val modoOscuro by ajustesDataStore.modoOscuroFlow.collectAsState(initial = false)
```

---

## 8. Navegación con Jetpack Navigation Compose

### 8.1 Configuración actual
La aplicación define sus rutas como cadenas de texto en un bloque `NavHost` situado generalmente en `MainActivity.kt`.

**Tabla de rutas:**
| Ruta | Pantalla | Descripción |
| :--- | :--- | :--- |
| `inicio` | PantallaInicio | Menú principal de la app. |
| `catalogo` | PantallaCatalogo | Lista de recetas guardadas localmente. |
| `agregar` | PantallaAgregar | Formulario para guardar una nueva receta. |
| `detalle/{recetaId}` | PantallaDetalle | Muestra una receta específica. Requiere un argumento. |
| `recetas_top` | PantallaRecetasTop | Consumo de la API externa (Retrofit). |
| `ajustes` | PantallaAjustes | Configuración (Modo oscuro). |

### 8.2 🔧 Cómo Agregar una Nueva Pantalla

**Paso 1: Crear el @Composable**
Crea tu pantalla, ej. `PantallaFavoritos.kt`:
```kotlin
@Composable
fun PantallaFavoritos(navController: NavController) {
    Text("Mis Favoritos")
}
```

**Paso 2: Agregar composable() al NavHost**
En tu `NavHost`:
```kotlin
// ANTES
NavHost(navController = navController, startDestination = "inicio") {
    composable("inicio") { PantallaInicio(navController) }
    // ...
}

// DESPUÉS
NavHost(navController = navController, startDestination = "inicio") {
    composable("inicio") { PantallaInicio(navController) }
    // ...
    composable("favoritos") { PantallaFavoritos(navController) } // Nuevo
}
```

**Paso 3: Agregar navegación desde otra pantalla**
En el botón de otra pantalla:
```kotlin
Button(onClick = { navController.navigate("favoritos") }) {
    Text("Ir a Favoritos")
}
```

### 8.3 Pasar Argumentos entre Pantallas

Para pasar un ID, la ruta incluye el argumento: `detalle/{recetaId}`.

```kotlin
composable(
    route = "detalle/{recetaId}",
    arguments = listOf(navArgument("recetaId") { type = NavType.IntType })
) { backStackEntry ->
    val recetaId = backStackEntry.arguments?.getInt("recetaId") ?: 0
    PantallaDetalle(recetaId = recetaId)
}
```
Al navegar, reemplazas el valor:
`navController.navigate("detalle/${receta.id}")`

---

## 9. Integración con la Cámara del Hardware

La aplicación puede tomar fotos para asignarlas a las recetas.

### 9.1 Configuración del FileProvider
Android restringe compartir URIs de archivos con otras apps (como la app de Cámara). Un `FileProvider` crea URIs temporales seguros.

*   En `AndroidManifest.xml` se define el provider.
*   En `res/xml/file_paths.xml`:
    ```xml
    <paths>
        <cache-path name="fotos_recetas" path="." />
    </paths>
    ```
    Esto permite a la cámara guardar en el directorio de caché de la app.
*   Se usa una función auxiliar `crearUriParaFoto()` (típicamente en el ViewModel o Utils) que usa `FileProvider.getUriForFile()`.

### 9.2 Permisos en Tiempo de Ejecución
Aunque en SDKs modernos la cámara lanzada vía Intent no siempre requiere el permiso `CAMERA`, la app debe gestionarlo si lo solicita explícitamente en el Manifest.
Se usa Compose `rememberLauncherForActivityResult` con `ActivityResultContracts.RequestPermission()`.

Flujo:
1. Usuario pulsa botón.
2. Si tiene permiso -> Crea URI y lanza cámara.
3. Si no tiene permiso -> Lanza solicitud. En caso de éxito, lanza cámara.

### 9.3 Captura de Imagen
Se utiliza `ActivityResultContracts.TakePicture()` pasándole el URI temporal generado. Si la foto se toma con éxito, el contrato devuelve `true` y el URI se convierte en String y se guarda en la Base de Datos Room (`imagenUri`).

---

## 10. Carga de Imágenes con Coil

Coil (Coroutine Image Loader) es la herramienta estándar en Compose.

```kotlin
import coil.compose.AsyncImage

AsyncImage(
    model = receta.imagenUri, // Puede ser un String ("content://...", "https://...")
    contentDescription = "Foto del platillo",
    contentScale = ContentScale.Crop // Recorta la imagen para llenar el espacio
)
```
Coil maneja automáticamente:
*   **Imágenes locales:** (URIs de Room tomadas por la cámara).
*   **Imágenes remotas:** (URLs devueltas por Retrofit en `RecetaRemota`).
*   Gestión de caché en disco y memoria.

---

## 11. Tema Dinámico (Modo Oscuro)

El tema de la app está dictado por el valor almacenado en `AjustesDataStore`.

1. En el punto de entrada, se observa el DataStore:
   `val modoOscuro by ajustesDataStore.modoOscuroFlow.collectAsState(initial = isSystemInDarkTheme())`
2. Se inyecta al Tema principal:
   `AppMovilFinalTheme(darkTheme = modoOscuro) { ... }`
3. El Theme interno define paletas:
   ```kotlin
   val colorScheme = if (darkTheme) {
       darkColorScheme(primary = Color.Blue, background = Color.Black) // Personaliza aquí
   } else {
       lightColorScheme(primary = Color.LightGray, background = Color.White)
   }
   MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
   ```

---

## 12. Configuración de Gradle

### 12.1 build.gradle.kts (proyecto)
Maneja plugins a nivel global.
*   `com.android.application` (AGP)
*   `org.jetbrains.kotlin.android` (Kotlin)

### 12.2 build.gradle.kts (app)
Contiene todas las dependencias.

*   `androidx.room:room-runtime`, `room-ktx`, y `room-compiler` (kapt/ksp) para DB.
*   `com.squareup.retrofit2:retrofit` y `converter-gson` para API.
*   `io.coil-kt:coil-compose` para imágenes.
*   `androidx.datastore:datastore-preferences` para configuración.
*   `androidx.navigation:navigation-compose` para rutas.

**Cómo agregar una librería:** Añadir la línea al bloque `dependencies {}` y dar clic en "Sync Now".

### 12.3 libs.versions.toml
En proyectos modernos (Gradle Version Catalogs), las dependencias y sus versiones se centralizan aquí. Si quieres actualizar Retrofit, buscas en este archivo, cambias la versión y sincronizas.

---

## 13. AndroidManifest.xml

Puntos clave:
*   `<uses-permission android:name="android.permission.INTERNET" />`: Requerido por Retrofit.
*   `<uses-permission android:name="android.permission.CAMERA" />`: Para tomar fotos.
*   `<uses-feature android:name="android.hardware.camera" android:required="false" />`: Para que Google Play no bloquee dispositivos sin cámara (opcional).
*   `<provider>`: Declaración obligatoria para el `FileProvider` con `authorities="${applicationId}.provider"`.
*   `<intent-filter>`: Configura `MainActivity` como `MAIN` y `LAUNCHER` (Pantalla de inicio).

---

## 14. Compilación y Despliegue

1.  **Compilación en depuración:** Presiona el botón ▶ (Run 'app') en la barra superior o `Shift + F10`.
2.  **Ejecutar en Emulador:** Asegúrate de tener creado un AVD (Android Virtual Device) en el Device Manager e inícialo antes de compilar.
3.  **Dispositivo Físico:**
    *   Activa "Opciones de desarrollador" en el celular (Toca 7 veces en "Número de compilación").
    *   Activa "Depuración por USB".
    *   Conecta el cable. El dispositivo aparecerá en la lista de Android Studio.
4.  **Generar APK Release:** Ve al menú `Build > Generate Signed Bundle / APK...`, sigue los pasos creando o utilizando un Keystore para firmar la aplicación y poder subirla a Google Play.

---

## 15. Referencia Rápida de Archivos

| Archivo | Ruta | Responsabilidad | Capa | Cuándo modificarlo |
| :--- | :--- | :--- | :--- | :--- |
| `Receta.kt` | `app/src/main/...` | Define modelo local Room. | Data Local | Para agregar campos a una receta (ej. categoría). |
| `RecetaDao.kt` | `app/src/main/...` | Consultas SQL de Room. | Data Local | Para crear nuevas queries de BD (ej. búsqueda). |
| `RecetaDatabase.kt`| `app/src/main/...` | Instancia de la BD. | Data Local | Al cambiar versiones de BD o agregar Entidades. |
| `RecetaRemota.kt` | `app/src/main/...` | Modelo de la API JSON. | Data Remota | Si la estructura JSON de la API cambia. |
| `RecetasApiService`| `app/src/main/...` | Endpoints Retrofit y Cliente | Data Remota | Para agregar nuevos endpoints o cambiar URL. |
| `AjustesDataStore` | `app/src/main/...` | Preferencias de usuario. | Data Local | Para guardar nuevas preferencias (ej. idioma). |
| `RecetaRepository` | `app/src/main/...` | Single Source of Truth. | Domain/Data | Al agregar nuevos métodos del DAO o API. |
| `RecetaViewModel` | `app/src/main/...` | Lógica de pantalla y estado | Presentación | Para agregar acciones de UI (ej. nuevo flujo). |
| `build.gradle.kts` | `app/` | Dependencias del módulo app. | Config. | Para instalar librerías nuevas (ej. Hilt, Mapas). |
| `AndroidManifest.xml`| `/app/src/main` | Manifiesto de Android. | Config. | Para pedir nuevos permisos (ej. ubicación). |

---
*Fin del Manual del Programador.*
