package cl.duoc.wim100.ui.viewmodel

import android.database.sqlite.SQLiteException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.wim100.data.repository.CandidataRepository
import cl.duoc.wim100.model.Candidata
import cl.duoc.wim100.validation.CandidataValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Campos de texto del formulario. Permite guardar valores y errores en un Map. */
enum class Campo { NOMBRE, CORREO, TELEFONO, ESPECIALIDAD, ANIOS, RESUMEN, PERFIL }

data class FormularioUiState(
    val valores: Map<Campo, String> = emptyMap(),
    val errores: Map<Campo, String> = emptyMap(),
    val cvUri: String? = null,
    val cvNombre: String? = null,
    val intentosFallidos: Int = 0,
    val guardando: Boolean = false,
    val mensajeError: String? = null,
    val guardada: Boolean = false        // true = la pantalla vuelve atrás
) {
    fun valor(campo: Campo): String = valores[campo].orEmpty()
}

class FormularioViewModel(private val repositorio: CandidataRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(FormularioUiState())
    val uiState: StateFlow<FormularioUiState> = _uiState.asStateFlow()

    /** Al escribir se borra el error de ese campo; se vuelve a validar al guardar. */
    fun cambiar(campo: Campo, valor: String) = _uiState.update {
        it.copy(valores = it.valores + (campo to valor), errores = it.errores - campo)
    }

    fun elegirCv(uri: String, nombre: String) = _uiState.update { it.copy(cvUri = uri, cvNombre = nombre) }

    fun quitarCv() = _uiState.update { it.copy(cvUri = null, cvNombre = null) }

    fun guardar(usuario: String) {
        val estado = _uiState.value
        val errores = Campo.entries
            .mapNotNull { campo -> validar(campo, estado.valor(campo))?.let { campo to it } }
            .toMap()
        if (errores.isNotEmpty()) {
            _uiState.update {
                it.copy(errores = errores, mensajeError = "Revisa los campos marcados", intentosFallidos = it.intentosFallidos + 1)
            }
            return
        }

        _uiState.update { it.copy(guardando = true, mensajeError = null) }
        viewModelScope.launch {
            try {
                repositorio.registrar(
                    Candidata(
                        nombre = estado.valor(Campo.NOMBRE).trim(),
                        correo = estado.valor(Campo.CORREO).trim().lowercase(),
                        telefono = estado.valor(Campo.TELEFONO).trim(),
                        especialidad = estado.valor(Campo.ESPECIALIDAD),
                        aniosExperiencia = estado.valor(Campo.ANIOS).trim().toInt(),   // ya validado
                        resumen = estado.valor(Campo.RESUMEN).trim(),
                        perfilUrl = estado.valor(Campo.PERFIL).trim(),
                        cvUri = estado.cvUri,
                        cvNombre = estado.cvNombre,
                        registradaPor = usuario
                    )
                )
                _uiState.update { it.copy(guardando = false, guardada = true) }
            } catch (e: SQLiteException) {
                // Los datos escritos se mantienen en pantalla para reintentar
                _uiState.update {
                    it.copy(guardando = false, mensajeError = "No se pudo guardar. Intenta de nuevo.", intentosFallidos = it.intentosFallidos + 1)
                }
            }
        }
    }

    private fun validar(campo: Campo, valor: String): String? = when (campo) {
        Campo.NOMBRE -> CandidataValidator.nombre(valor)
        Campo.CORREO -> CandidataValidator.correo(valor)
        Campo.TELEFONO -> CandidataValidator.telefono(valor)
        Campo.ESPECIALIDAD -> CandidataValidator.especialidad(valor)
        Campo.ANIOS -> CandidataValidator.anios(valor)
        Campo.RESUMEN -> CandidataValidator.resumen(valor)
        Campo.PERFIL -> CandidataValidator.perfil(valor)
    }.mensaje
}
