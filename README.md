Mis Recetas App 

Una aplicación móvil Android nativa y completa diseñada para gestionar recetas de cocina de forma local y explorar recetas globales mediante conexión a internet. 

Descripción de la App
"Mis Recetas" permite al usuario guardar sus platillos favoritos con fotos tomadas directamente desde la cámara del hardware, gestionar una lista local, y navegar por un catálogo mundial de recetas. Además, incluye soporte nativo para Modo Oscuro gestionado por el usuario.

Arquitectura Elegida
El proyecto fue construido utilizando **MVVM (Model-View-ViewModel)** combinado con el **Patrón Repositorio (Clean Architecture)**. 

*   **Capa UI:** Desarrollada 100% con Jetpack Compose, utilizando navegación reactiva y `LazyColumn` para el manejo eficiente de listas dinámicas.
*   **ViewModel:** Centraliza la lógica de negocio y expone los estados a la interfaz visual utilizando flujos reactivos (`StateFlow`).
*   **Repositorio:** Actúa como mediador central aplicando el principio de abstracción. La UI no conoce el origen de los datos; solo le pide información al repositorio.
*   **Corrutinas:** Se utilizaron flujos asíncronos para todas las operaciones pesadas, asegurando un rendimiento óptimo sin congelar la aplicación.

*Fuentes de Datos*
1.  **Persistencia Local (Room):** Se utilizó Room para el almacenamiento estructurado de recetas locales, implementando un DAO robusto.
2.  **Persistencia de Preferencias (DataStore):** Manejo de las configuraciones del usuario (Modo Oscuro) de manera reactiva y segura.
3.  **Consumo de API REST (Retrofit):** 
    *   **API Utilizada:** [DummyJSON Recipes API](https://dummyjson.com/docs/recipes)
    *   Manejo completo de asincronía incluyendo los estados visuales de "Cargando", "Éxito" y "Error de Red".

*Capturas de Pantalla*
*   *Captura 1: Pantalla de Inicio*
*   <img width="1080" height="2400" alt="WhatsApp Image 2026-08-16 at 9 46 36 PM" src="https://github.com/user-attachments/assets/e61caf89-e9e6-4fb6-8ad8-68b7b1c93321" />

*   *Captura 2: Pantalla del Catálogo*
*   <img width="720" height="1600" alt="WhatsApp Image 2026-08-16 at 9 46 36 PM (1)" src="https://github.com/user-attachments/assets/4754d8b5-29df-4dd7-8784-aaf371af53bb" />

*   *Captura 3: Pantalla de Agregar*
* <img width="1080" height="2400" alt="WhatsApp Image 2026-08-16 at 9 46 37 PM" src="https://github.com/user-attachments/assets/abd8caa2-2a0c-46db-a00a-fd87888f5ae2" />

*   *Captura 4: Pantalla Recetas Top mostrando el consumo de API*
*   <img width="720" height="1600" alt="WhatsApp Image 2026-08-16 at 9 46 37 PM (1)" src="https://github.com/user-attachments/assets/cf018807-88f4-45e7-89fb-123198820d36" />

*   *Captura 5: Pantalla de Ajustes (Modo Oscuro activado)*
<img width="1080" height="2400" alt="WhatsApp Image 2026-08-16 at 9 46 37 PM (2)" src="https://github.com/user-attachments/assets/cbc2c78c-2d33-49ee-9654-9605ce2e9896" />

*   *Captura Diagrama*
<img width="4702" height="4319" alt="Jetpack Compose Recipe Flow-2026-08-26-130155" src="https://github.com/user-attachments/assets/55b0fea5-26f2-4b48-8e05-90797d9e95b6" />


  
*Ney Jurado* - Proyecto Final de Aplicaciones Móviles.
