package cl.duoc.wim100.ui.viewmodel

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.wim100.WimApplication
import cl.duoc.wim100.di.AppContainer

/**
 * Fábrica única de ViewModels: le entrega a cada uno los repositorios que necesita desde el AppContainer.
 * Uso en una pantalla: viewModel(factory = AppViewModelProvider.Factory)
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { SesionViewModel(contenedor().historialRepository) }
        initializer { CandidatasViewModel(contenedor().candidataRepository) }
        initializer { FormularioViewModel(contenedor().candidataRepository) }
        initializer {
            DetalleViewModel(createSavedStateHandle(), contenedor().candidataRepository, contenedor().evaluacionRepository)
        }
        initializer {
            EvaluacionViewModel(createSavedStateHandle(), contenedor().candidataRepository, contenedor().evaluacionRepository)
        }
        initializer { ResultadosViewModel(contenedor().evaluacionRepository) }
        initializer { HistorialViewModel(contenedor().historialRepository) }
    }
}

private fun CreationExtras.contenedor(): AppContainer = (this[APPLICATION_KEY] as WimApplication).container
