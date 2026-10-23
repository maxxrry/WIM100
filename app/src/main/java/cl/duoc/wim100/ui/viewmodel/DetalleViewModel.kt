package cl.duoc.wim100.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.wim100.data.repository.CandidataRepository
import cl.duoc.wim100.data.repository.EvaluacionRepository
import cl.duoc.wim100.model.Candidata
import cl.duoc.wim100.model.EstadoCandidata
import cl.duoc.wim100.model.Evaluacion
import cl.duoc.wim100.model.Puntaje
import cl.duoc.wim100.navigation.Rutas
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Una evaluación junto con sus 5 puntajes, ordenados como el enum Criterio. */
data class EvaluacionDetalle(val evaluacion: Evaluacion, val puntajes: List<Puntaje>)

data class DetalleUiState(
    val candidata: Candidata? = null,
    val evaluaciones: List<EvaluacionDetalle> = emptyList(),
    val cargando: Boolean = true
) {
    val promedio: Double? get() = evaluaciones.map { it.evaluacion.puntajeTotal }.takeIf { it.isNotEmpty() }?.average()
}

class DetalleViewModel(
    savedStateHandle: SavedStateHandle,
    private val candidatas: CandidataRepository,
    evaluaciones: EvaluacionRepository
) : ViewModel() {

    private val id: Long = checkNotNull(savedStateHandle[Rutas.ARG_ID])

    /** Une tres consultas: la candidata, sus evaluaciones y los puntajes de cada una. */
    val uiState: StateFlow<DetalleUiState> = combine(
        candidatas.observar(id),
        evaluaciones.observarDeCandidata(id),
        evaluaciones.observarPuntajes(id)
    ) { candidata, lista, puntajes ->
        val porEvaluacion = puntajes.groupBy { it.evaluacionId }
        DetalleUiState(
            candidata = candidata,
            evaluaciones = lista.map { ev ->
                EvaluacionDetalle(ev, porEvaluacion[ev.id].orEmpty().sortedBy { it.criterio.ordinal })
            },
            cargando = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetalleUiState())

    /** La pantalla abre el PDF (necesita Context) y avisa aquí para dejarlo en el historial. */
    fun registrarCvAbierto(usuario: String) {
        val candidata = uiState.value.candidata ?: return
        viewModelScope.launch { candidatas.registrarCvAbierto(candidata, usuario) }
    }

    /** Solo Dirección; la pantalla muestra los botones cuando el estado es Evaluada. */
    fun decidir(decision: EstadoCandidata, usuario: String) {
        viewModelScope.launch {
            try {
                candidatas.decidir(id, decision, usuario)
            } catch (e: IllegalStateException) {
                // Doble toque: la primera llamada ya cerró la postulación, la pantalla ya muestra el estado nuevo
            }
        }
    }
}
