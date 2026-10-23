package cl.duoc.wim100.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.wim100.data.repository.CandidataRepository
import cl.duoc.wim100.model.Candidata
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class CandidatasUiState(
    val busqueda: String = "",
    val candidatas: List<Candidata> = emptyList(),
    val cargando: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
class CandidatasViewModel(repositorio: CandidataRepository) : ViewModel() {

    private val busqueda = MutableStateFlow("")

    /**
     * Cada vez que cambia el texto, flatMapLatest cancela la consulta anterior y lanza una nueva.
     * Como Room devuelve Flow, la lista también se actualiza sola al registrar una candidata.
     */
    val uiState: StateFlow<CandidatasUiState> =
        combine(busqueda, busqueda.flatMapLatest { repositorio.buscar(it) }) { texto, lista ->
            CandidatasUiState(busqueda = texto, candidatas = lista, cargando = false)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CandidatasUiState())

    fun buscar(texto: String) {
        busqueda.value = texto
    }
}
