package cl.duoc.wim100.validation

/** Resultado de validar un campo: Ok, o Error con el mensaje que verá la usuaria. */
sealed class ValidationResult {
    data object Ok : ValidationResult()
    data class Error(val texto: String) : ValidationResult()

    val esValido: Boolean get() = this is Ok
    val mensaje: String? get() = (this as? Error)?.texto
}
