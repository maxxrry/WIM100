package cl.duoc.wim100.validation

import cl.duoc.wim100.model.Rol

object LoginValidator {

    private val SOLO_LETRAS = Regex("^[\\p{L} ]+$")   // \p{L} = cualquier letra, incluye tildes y ñ

    fun nombre(valor: String): ValidationResult {
        val v = valor.trim()
        return when {
            v.isEmpty() -> ValidationResult.Error("Ingresa tu nombre")
            v.length < 3 -> ValidationResult.Error("Mínimo 3 caracteres")
            !SOLO_LETRAS.matches(v) -> ValidationResult.Error("Solo letras y espacios")
            else -> ValidationResult.Ok
        }
    }

    fun rol(valor: Rol?): ValidationResult =
        if (valor == null) ValidationResult.Error("Elige un rol") else ValidationResult.Ok
}
