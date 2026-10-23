package cl.duoc.wim100.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.wim100.data.repository.HistorialRepository
import cl.duoc.wim100.model.Rol
import cl.duoc.wim100.model.Sesion
import cl.duoc.wim100.model.TipoAccion
import cl.duoc.wim100.validation.LoginValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val nombre: String = "",
    val rol: Rol? = null,
    val errorNombre: String? = null,
    val errorRol: String? = null,
    val intentosFallidos: Int = 0,
    val sesion: Sesion? = null          // distinto de null = ya ingresó
)

/** Login y sesión activa. Vive mientras vive la actividad, así todas las pantallas saben quién ingresó. */
class SesionViewModel(private val historial: HistorialRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun cambiarNombre(valor: String) = _uiState.update { it.copy(nombre = valor, errorNombre = null) }

    fun cambiarRol(rol: Rol) = _uiState.update { it.copy(rol = rol, errorRol = null) }

    fun ingresar() {
        val estado = _uiState.value
        val errorNombre = LoginValidator.nombre(estado.nombre).mensaje
        val errorRol = LoginValidator.rol(estado.rol).mensaje
        val rol = estado.rol
        if (errorNombre != null || rol == null) {
            _uiState.update {
                it.copy(errorNombre = errorNombre, errorRol = errorRol, intentosFallidos = it.intentosFallidos + 1)
            }
            return
        }
        val sesion = Sesion(estado.nombre.trim().replace(Regex("\\s+"), " "), rol)
        _uiState.update { it.copy(sesion = sesion) }
        viewModelScope.launch { historial.registrar(TipoAccion.INICIO_SESION, rol.etiqueta, sesion.nombre) }
    }

    fun cerrarSesion() = _uiState.update { LoginUiState() }
}
