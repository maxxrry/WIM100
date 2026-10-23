package cl.duoc.wim100.navigation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.wim100.model.Rol
import cl.duoc.wim100.nativo.NotificacionHelper
import cl.duoc.wim100.ui.screens.CandidatasScreen
import cl.duoc.wim100.ui.screens.FormularioScreen
import cl.duoc.wim100.ui.screens.LoginScreen
import cl.duoc.wim100.ui.viewmodel.AppViewModelProvider
import cl.duoc.wim100.ui.viewmodel.CandidatasViewModel
import cl.duoc.wim100.ui.viewmodel.FormularioViewModel
import cl.duoc.wim100.ui.viewmodel.SesionViewModel

/** Pestañas de la barra inferior. */
private data class Pestana(val ruta: String, val titulo: String, val icono: ImageVector)

private val pestanas = listOf(
    Pestana(Rutas.CANDIDATAS, "Candidatas", Icons.Filled.Groups),
    Pestana(Rutas.RESULTADOS, "Resultados", Icons.Filled.Leaderboard),
    Pestana(Rutas.HISTORIAL, "Historial", Icons.Filled.History)
)

private const val DURACION = 300

/**
 * Estructura de la app: barras + NavHost.
 * SesionViewModel se crea aquí (fuera del NavHost) para que viva mientras viva la actividad.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WimNavHost(sesionViewModel: SesionViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val navController = rememberNavController()
    val login by sesionViewModel.uiState.collectAsStateWithLifecycle()
    val sesion = login.sesion
    val rutaActual = navController.currentBackStackEntryAsState().value?.destination?.route
    val esPestana = pestanas.any { it.ruta == rutaActual }

    // La sesión manda: al ingresar se va a Candidatas; sin sesión siempre se vuelve al Login
    LaunchedEffect(sesion) {
        val enLogin = navController.currentDestination?.route == Rutas.LOGIN
        if (sesion != null && enLogin) {
            navController.navigate(Rutas.CANDIDATAS) { popUpTo(Rutas.LOGIN) { inclusive = true } }
        } else if (sesion == null && !enLogin) {
            navController.navigate(Rutas.LOGIN) { popUpTo(navController.graph.id) { inclusive = true } }
        }
    }

    if (sesion != null) NotificacionHelper.SolicitarPermisoNotificaciones()

    Scaffold(
        topBar = {
            if (esPestana && sesion != null) {
                TopAppBar(
                    title = {
                        Column {
                            Text("WIM 100")
                            Text(
                                "${sesion.nombre} · ${sesion.rol.etiqueta}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = sesionViewModel::cerrarSesion) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (esPestana) {
                NavigationBar {
                    pestanas.forEach { pestana ->
                        NavigationBarItem(
                            selected = rutaActual == pestana.ruta,
                            onClick = { irAPestana(navController, pestana.ruta) },
                            icon = { Icon(pestana.icono, contentDescription = null) },
                            label = { Text(pestana.titulo) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.LOGIN,
            // consumeWindowInsets: las pantallas con Scaffold propio no suman el margen dos veces
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
            // Avanzar: entra desde la derecha. Volver: sale hacia la derecha.
            enterTransition = { slideIntoContainer(SlideDirection.Start, tween(DURACION)) + fadeIn(tween(DURACION)) },
            exitTransition = { slideOutOfContainer(SlideDirection.Start, tween(DURACION)) + fadeOut(tween(DURACION)) },
            popEnterTransition = { slideIntoContainer(SlideDirection.End, tween(DURACION)) + fadeIn(tween(DURACION)) },
            popExitTransition = { slideOutOfContainer(SlideDirection.End, tween(DURACION)) + fadeOut(tween(DURACION)) }
        ) {
            composable(Rutas.LOGIN) {
                LoginScreen(
                    estado = login,
                    alCambiarNombre = sesionViewModel::cambiarNombre,
                    alElegirRol = sesionViewModel::cambiarRol,
                    alIngresar = sesionViewModel::ingresar
                )
            }
            composable(Rutas.CANDIDATAS) {
                val vm: CandidatasViewModel = viewModel(factory = AppViewModelProvider.Factory)
                val estado by vm.uiState.collectAsStateWithLifecycle()
                CandidatasScreen(
                    estado = estado,
                    puedeRegistrar = sesion?.rol == Rol.EVALUADORA,
                    alBuscar = vm::buscar,
                    alAbrir = { id -> navController.navigate(Rutas.detalle(id)) },
                    alRegistrar = { navController.navigate(Rutas.FORMULARIO) }
                )
            }
            composable(Rutas.FORMULARIO) {
                val vm: FormularioViewModel = viewModel(factory = AppViewModelProvider.Factory)
                val estado by vm.uiState.collectAsStateWithLifecycle()
                // Guardada con éxito: volver a la lista (la nueva candidata aparece sola)
                LaunchedEffect(estado.guardada) { if (estado.guardada) navController.popBackStack() }
                FormularioScreen(
                    estado = estado,
                    alCambiar = vm::cambiar,
                    alElegirCv = vm::elegirCv,
                    alQuitarCv = vm::quitarCv,
                    alGuardar = { vm.guardar(sesion?.nombre.orEmpty()) },
                    alVolver = { navController.popBackStack() }
                )
            }
            composable(Rutas.DETALLE, arguments = listOf(navArgument(Rutas.ARG_ID) { type = NavType.LongType })) {
                EnConstruccion("Detalle")
            }
            composable(Rutas.EVALUACION, arguments = listOf(navArgument(Rutas.ARG_ID) { type = NavType.LongType })) {
                EnConstruccion("Evaluación")
            }
            composable(Rutas.RESULTADOS) { EnConstruccion("Resultados") }
            composable(Rutas.HISTORIAL) { EnConstruccion("Historial") }
        }
    }
}

/** Cambia de pestaña sin apilar pantallas repetidas y recordando el scroll de cada una. */
private fun irAPestana(navController: NavHostController, ruta: String) {
    navController.navigate(ruta) {
        popUpTo(Rutas.CANDIDATAS) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

// shortcut: marcador temporal; cada paso de pantallas reemplaza una de estas líneas y al final se borra
@Composable
private fun EnConstruccion(nombre: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("$nombre: en construcción") }
}
