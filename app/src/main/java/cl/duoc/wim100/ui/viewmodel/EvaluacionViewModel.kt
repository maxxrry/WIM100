package cl.duoc.wim100.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.wim100.data.repository.CandidataRepository
import cl.duoc.wim100.data.repository.EvaluacionDuplicadaException
import cl.duoc.wim100.data.repository.EvaluacionRepository
import cl.duoc.wim100.model.CalculadoraPuntaje
import cl.duoc.wim100.model.Criterio
import cl.duoc.wim100.navigation.Rutas
import cl.duoc.wim100.validation.EvaluacionValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EvaluacionUiState(
    val nombreCandidata: String = "",
    val puntajes: Map<Criterio, Int> = emptyMap(),
    val observaciones: Map<Criterio, String> = emptyMap(),
    val comentario: String = "",
    val erroresCriterio: Map<Criterio, String> = emptyMap(),
    val errorComentario: String? = null,
    val mensajeError: String? = null,     // error general (ej: evaluación duplicada)
    val intentosFallidos: Int = 0,
    val guardando: Boolean = false,
    val totalGuardado: Int? = null        // distinto de null = guardada: notificar y volver
) {
    /** Se recalcula en cada recomposición: el total se ve "en vivo" mientras se puntúa. */
    val totalEnVivo: Int get() = CalculadoraPuntaje.total(puntajes)
}

class EvaluacionViewModel(
    savedStateHandle: SavedStateHandle,
    candidatas: CandidataRepository,
    private val evaluaciones: EvaluacionRepository
) : ViewModel() {

    private val id: Long = checkNotNull(savedStateHandle[Rutas.ARG_ID])

    private val _uiState = MutableStateFlow(EvaluacionUiState())
    val uiState: StateFlow<EvaluacionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val nombre = candidatas.observar(id).first()?.nombre.orEmpty()
            _uiState.update { it.copy(nombreCandidata = nombre) }
        }
    }

    fun elegirPuntaje(criterio: Criterio, valor: Int) = _uiState.update {
        it.copy(puntajes = it.puntajes + (criterio to valor), erroresCriterio = it.erroresCriterio - criterio)
    }

    fun cambiarObservacion(criterio: Criterio, texto: String) = _uiState.update {
        it.copy(observaciones = it.observaciones + (criterio to texto), erroresCriterio = it.erroresCriterio - criterio)
    }

    fun cambiarComentario(texto: String) = _uiState.update { it.copy(comentario = texto, errorComentario = null) }

    fun guardar(evaluadora: String) {
        val estado = _uiState.value
        // Por criterio se muestra un solo error: primero el del puntaje, luego el de la observación
        val errores = Criterio.entries.mapNotNull { criterio ->
            val puntaje = estado.puntajes[criterio]
            val error = EvaluacionValidator.puntaje(puntaje).mensaje
                ?: EvaluacionValidator.observacion(puntaje, estado.observaciones[criterio].orEmpty()).mensaje
            error?.let { criterio to it }
        }.toMap()
        val errorComentario = EvaluacionValidator.comentario(estado.comentario).mensaje

        if (errores.isNotEmpty() || errorComentario != null) {
            _uiState.update {
                it.copy(
                    erroresCriterio = errores, errorComentario = errorComentario,
                    mensajeError = "Revisa los campos marcados", intentosFallidos = it.intentosFallidos + 1
                )
            }
            return
        }

        _uiState.update { it.copy(guardando = true, mensajeError = null) }
        viewModelScope.launch {
            try {
                val total = evaluaciones.guardar(id, evaluadora, estado.puntajes, estado.observaciones, estado.comentario)
                _uiState.update { it.copy(guardando = false, totalGuardado = total) }
            } catch (e: EvaluacionDuplicadaException) {
                fallo(e.message)
            } catch (e: IllegalStateException) {
                fallo(e.message)   // candidata cerrada por Dirección o inexistente
            }
        }
    }

    private fun fallo(mensaje: String?) = _uiState.update {
        it.copy(guardando = false, mensajeError = mensaje, intentosFallidos = it.intentosFallidos + 1)
    }
}
