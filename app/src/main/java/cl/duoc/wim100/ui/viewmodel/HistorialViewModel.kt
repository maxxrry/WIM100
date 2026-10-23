package cl.duoc.wim100.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.wim100.data.repository.HistorialRepository
import cl.duoc.wim100.model.RegistroHistorial
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HistorialUiState(
    val registros: List<RegistroHistorial> = emptyList(),
    val cargando: Boolean = true
)

class HistorialViewModel(historial: HistorialRepository) : ViewModel() {

    val uiState: StateFlow<HistorialUiState> = historial.observarTodo()
        .map { HistorialUiState(registros = it, cargando = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistorialUiState())
}
