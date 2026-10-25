# WIM 100 · Evaluación de candidatas

App Android (MVP académico) para el caso **WIM 100 – DYM Cyber**: registrar postulantes al programa,
evaluarlas con una matriz de criterios, evitar evaluaciones duplicadas y que Dirección decida con
resultados consolidados.

Asignatura **DSY1105 Desarrollo de Aplicaciones Móviles** · Duoc UC · 2026.

> **Datos ficticios.** Todas las personas, correos (`@ejemplo.cl` / `@test.cl`), teléfonos y perfiles
> de LinkedIn son inventados. La matriz de evaluación es provisional.

## Integrantes

| Nombre | GitHub | Aporte principal |
|---|---|---|
| maximiliano serey |@maxxrry | desarrollador |
| vicente muñoz | @SK1xez | desarrollador |

## Tecnologías

Kotlin 2.2.10 · Jetpack Compose + Material Design 3 · Room 2.8.4 (KSP) · Navigation Compose 2.9.8 ·
ViewModel + StateFlow · minSdk 24 · compileSdk/targetSdk 37.

## Qué hace la app

| Necesidad del caso | Cómo se resolvió |
|---|---|
| Ingreso con perfiles | Login con nombre + rol: **Evaluadora** (registra y evalúa) o **Dirección** (revisa y decide) |
| Registrar y consultar candidatas | Formulario con 7 campos validados + CV en PDF opcional; lista con búsqueda por nombre o especialidad |
| Matriz de evaluación | 5 criterios con **igual peso**, puntaje 1–5 cada uno; total 0–100 que se calcula en vivo |
| Justificar puntajes bajos | Observación **obligatoria** si un criterio tiene puntaje ≤ 2 |
| Evitar duplicados | Una evaluadora no puede evaluar dos veces a la misma candidata (doble barrera, ver abajo) |
| Estado de la postulación | Recibida → En revisión (1 evaluación) → Evaluada (2) → Seleccionada / No seleccionada |
| Resultados consolidados | Ranking por promedio con barras animadas |
| Trazabilidad | Historial de acciones: ingresos, registros, evaluaciones, duplicados bloqueados, decisiones y CV abiertos |

### Matriz WIM 100

| Criterio | Qué mide |
|---|---|
| Formación y certificaciones | Estudios, cursos y certificaciones en ciberseguridad |
| Experiencia práctica | Proyectos, auditorías o incidentes en los que ha participado |
| Impacto en su organización | Resultados concretos en su equipo o empresa |
| Mentoría y redes | Apoyo a otras mujeres y participación en comunidades |
| Proyección y motivación | Metas claras y potencial de crecimiento en el programa |

**Total = suma de puntajes ÷ (5 criterios × 5) × 100**, es decir, *suma × 4*.
Ejemplo: 5 + 4 + 3 + 2 + 1 = 15 → **60 puntos**.

## Requisitos técnicos y dónde encontrarlos

| Requisito | Archivos |
|---|---|
| Material Design 3 y tema propio | `ui/theme/` (paleta azul noche + ámbar, modo claro y oscuro) |
| Navegación | `navigation/Rutas.kt`, `navigation/WimNavHost.kt` (barra inferior con 3 pestañas) |
| Formularios con ícono y error visual | `ui/components/CampoTexto.kt`, `ui/screens/FormularioScreen.kt`, `EvaluacionScreen.kt` |
| Validación fuera de la UI | `validation/` (Kotlin puro) → usada por los ViewModels |
| Arquitectura MVVM | `ui/screens/` → `ui/viewmodel/` → `data/repository/` → `data/local/dao/` |
| Persistencia local | `data/local/WimDatabase.kt` (Room, 4 tablas) + `DatosSemilla.kt` |
| Recursos nativos | `nativo/ArchivoHelper.kt`, `nativo/NotificacionHelper.kt` |
| Pruebas | `app/src/test/` (unitarias) y `app/src/androidTest/` (Room en memoria) |

### Animaciones

| Animación | Dónde |
|---|---|
| Transición deslizante entre pantallas | `WimNavHost.kt` (`enterTransition`, `popExitTransition`…) |
| Botón que se sacude ante un error | `ui/components/BotonSacudible.kt` (`Animatable`) |
| Mensaje de error que aparece y desaparece | `ui/components/CampoTexto.kt` → `MensajeError` (`AnimatedVisibility`) |
| Total de la matriz que "corre" hasta el valor nuevo | `EvaluacionScreen.kt` (`animateIntAsState`) |
| Barras del ranking que crecen | `ResultadosScreen.kt` (`animateFloatAsState`) |
| Tarjetas que se reacomodan al buscar | `CandidatasScreen.kt` (`animateItem`) |

### Recursos nativos y permisos

| Recurso | Permiso | Cómo se maneja |
|---|---|---|
| **Selector de documentos (SAF)** para adjuntar el CV y **abrirlo con otra app** (intent `ACTION_VIEW`) | Ninguno: la usuaria elige el archivo y la app solo accede a ese | Se conserva el permiso de lectura del archivo; si no hay lector de PDF se muestra un mensaje en vez de cerrarse |
| **Notificación local** al guardar una evaluación | `POST_NOTIFICATIONS`, pedido en tiempo de ejecución (Android 13+) | Se pide al ingresar. **Si se rechaza, la app funciona igual**: simplemente no muestra el aviso |

## Arquitectura

```
cl.duoc.wim100
├── model/            Entidades Room, enums (Rol, EstadoCandidata, Criterio, TipoAccion), CalculadoraPuntaje
├── data/local/       WimDatabase, DAOs, DatosSemilla
├── data/repository/  CandidataRepository, EvaluacionRepository, HistorialRepository
├── validation/       LoginValidator, CandidataValidator, EvaluacionValidator, ValidationResult
├── nativo/           ArchivoHelper, NotificacionHelper
├── navigation/       Rutas, WimNavHost
├── di/               AppContainer (inyección de dependencias manual)
└── ui/
    ├── theme/        Colores, tipografía y tema
    ├── components/   CampoTexto, BotonSacudible, ChipEstado, SelectorPuntaje
    ├── viewmodel/    Un ViewModel por pantalla + AppViewModelProvider
    └── screens/      Login, Candidatas, Formulario, Detalle, Evaluación, Resultados, Historial
```

**Flujo:** la pantalla observa un `UiState` (`StateFlow`) con `collectAsStateWithLifecycle()` → al tocar
algo llama al ViewModel → el ViewModel valida y llama al Repository → el Repository usa el DAO de Room →
Room emite un `Flow` nuevo → la pantalla se actualiza sola.

### Decisiones de diseño

- **Criterios como `enum`, no como tabla.** Con pesos iguales cada criterio es solo nombre y descripción,
  así que no necesita tabla, DAO ni datos semilla.
- **Duplicados con doble barrera.** (1) `EvaluacionRepository` consulta si ya existe la evaluación antes de
  guardar; (2) el índice único `(candidataId, evaluadora)` en Room la rechaza aunque la consulta fallara.
  La columna `evaluadora` usa `COLLATE NOCASE`, así "Paula Ríos" y "paula ríos" son la misma persona.
  Cada intento bloqueado queda en el historial.
- **Evaluación en una transacción.** Evaluación, puntajes, cambio de estado e historial se guardan juntos:
  si algo falla no se guarda nada.
- **Historial sin clave foránea.** Así puede registrar acciones sin candidata (inicio de sesión) y nunca se
  pierde.
- **Dirección solo decide sobre candidatas Evaluadas.** La pantalla oculta los botones y además el
  Repository lo verifica, con un diálogo de confirmación porque la decisión no se puede deshacer.
- **Los ViewModels no reciben `Context`.** Lo que necesita Android (abrir PDF, notificar) lo hace la
  pantalla, y el ViewModel solo registra el resultado.

**Limitación conocida:** `NOCASE` de SQLite solo ignora mayúsculas en letras sin tilde, por lo que
"RÍOS" y "ríos" se consideran distintos.

## Cómo ejecutar

1. Abrir la carpeta en Android Studio (con JDK 21) y esperar la sincronización de Gradle.
2. Ejecutar en un emulador o teléfono con **Android 7.0 (API 24) o superior**.
   Para ver el permiso de notificaciones se recomienda Android 13+.
3. Pruebas:
   - Unitarias (validadores y puntaje): `./gradlew test`
   - Instrumentadas (duplicados en Room, requiere emulador): `./gradlew connectedDebugAndroidTest`

### Datos de prueba

Al abrir la app por primera vez se cargan 5 candidatas y 5 evaluaciones de **Paula Ríos** y **Carla Méndez**:

| Candidata | Especialidad | Estado inicial |
|---|---|---|
| Catalina Fuentes Araya | Pentesting | Evaluada (2) |
| Valentina Rojas Medina | Operaciones SOC | Evaluada (2) |
| Antonia Muñoz Cárdenas | Cumplimiento normativo | En revisión (1, de Paula Ríos) |
| Sofía Espinoza Vera | Seguridad en la nube | Recibida |
| Camila Torres Pizarro | Análisis de malware | Recibida |

## Guion de demostración

1. Ingresar vacío → el botón se sacude y aparecen los errores. Ingresar como **Paula Ríos** (Evaluadora) y
   aceptar las notificaciones.
2. **Nueva candidata** con datos inválidos (correo `@gmail.com`) → errores por campo. Corregir, adjuntar un
   PDF y guardar.
3. Abrir la candidata → **Abrir CV** con la app de PDF del teléfono.
4. Abrir a **Antonia Muñoz** → Evaluar → poner un 2 sin observación (error) → completar y guardar →
   **"Ya evaluaste a esta candidata"** (duplicado bloqueado).
5. Cerrar sesión, ingresar como **Carla Méndez** y evaluar a Antonia → notificación y estado **Evaluada**.
6. Ingresar como **Dirección** → Resultados → Antonia → **Seleccionar** (pide confirmación).
7. **Historial**: aparecen todas las acciones, con el duplicado en rojo.
8. Cerrar y volver a abrir la app: los datos siguen ahí (Room).
