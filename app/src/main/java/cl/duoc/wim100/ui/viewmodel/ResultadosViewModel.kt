package cl.duoc.wim100.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.wim100.data.repository.EvaluacionRepository
import cl.duoc.wim100.model.ResultadoCandidata
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ResultadosUiState(
    val ranking: List<ResultadoCandidata> = emptyList(),
    val cargando: Boolean = true
)

class ResultadosViewModel(evaluaciones: EvaluacionRepository) : ViewModel() {

    val uiState: StateFlow<ResultadosUiState> = evaluaciones.observarResultados()
        .map { ResultadosUiState(ranking = it, cargando = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ResultadosUiState())
}
