# CLAUDE.md · Proyecto WIM 100 (DSY1105)

## Contexto
MVP académico Android para el caso WIM 100 – DYM Cyber (Duoc UC, DSY1105).
Equipo de 2 integrantes. Entrega: lunes 26 de octubre de 2026. Después hay una defensa individual SIN IA,
así que cada cambio debe ser entendible y explicable por las integrantes.

## Stack
- Kotlin 2.2.10, Jetpack Compose, Material Design 3
- Room 2.8.4 con KSP, Navigation Compose, lifecycle-viewmodel-compose
- minSdk 24, compileSdk/targetSdk 37, JDK 21
- Paquete base: `cl.duoc.wim100` (NO usar `com.example.appsemestral`)

## Referencia
Existe un proyecto de referencia del docente en `../Proyecto_Kotlin_WIM100` (solo lectura).
- Úsalo para entender la arquitectura y las versiones de dependencias.
- NO copies archivos completos. Escribe una versión propia: nombres, datos semilla, colores,
  textos y criterios de la matriz deben ser distintos.
- Nunca modifiques archivos dentro de esa carpeta.

## Arquitectura (MVVM, obligatoria)
```
cl.duoc.wim100/
├── model/            Entidades Room, enums, CalculadoraPuntaje
├── data/local/       WimDatabase, Converters, DatosSemilla, dao/
├── data/repository/  CandidataRepository, EvaluacionRepository, HistorialRepository
├── validation/       Validadores en Kotlin puro (sin Android) + ValidationResult
├── nativo/           ArchivoHelper (selector PDF + intent), NotificacionHelper
├── navigation/       Rutas, NavHost
├── di/               AppContainer (DI manual)
└── ui/
    ├── theme/  components/  screens/  viewmodel/
```
Reglas:
- Screen → ViewModel → Repository → DAO. Ninguna capa se salta a otra.
- Las validaciones van en `validation/`, nunca dentro de composables.
- El estado de cada pantalla es un `UiState` en `StateFlow`; la UI usa `collectAsStateWithLifecycle()`.
- Los ViewModels no reciben `Context`.

## Alcance (no agregar más sin que lo pidan)
- Roles: Evaluadora y Dirección (login con nombre + rol, sin biometría).
- Pantallas: Login, Candidatas (búsqueda), Formulario, Detalle, Evaluación, Resultados, Historial.
- Matriz 1–5 por criterio, observación obligatoria si puntaje ≤ 2, total 0–100.
- Duplicados: verificación en el Repository + índice único (candidataId, evaluadora).
- Historial de acciones (sin clave foránea).
- Recursos nativos: (1) selector de PDF con SAF + abrir con intent, (2) notificación local con permiso
  POST_NOTIFICATIONS en tiempo de ejecución (la app debe funcionar si se rechaza).
- Animaciones: transiciones del NavHost, botón que se sacude con error, mensaje de error con AnimatedVisibility.
- Solo datos ficticios (dominios ejemplo.cl / test.cl, enlaces linkedin simulados).

## Forma de trabajo
- Responder en español.
- Trabajar en pasos pequeños: un archivo o una capa por vez. Antes de escribir, explicar brevemente qué
  se va a hacer y por qué; después, resumir qué hace cada parte del código.
- Código simple y legible antes que ingenioso. Comentarios breves en español solo donde ayuden a explicar.
- Al terminar cada paso: compilar (`./gradlew assembleDebug`) y, si hay validadores, `./gradlew test`.
- Proponer un mensaje de commit técnico (feat:, fix:, test:, docs:), pero NO hacer commit ni push:
  cada integrante hace sus propios commits.
- No tocar el repositorio después del 26 de octubre.
