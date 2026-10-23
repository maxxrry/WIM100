package cl.duoc.wim100.validation

import cl.duoc.wim100.model.CalculadoraPuntaje

object EvaluacionValidator {

    const val PUNTAJE_BAJO = 2          // puntajes ≤ 2 exigen observación
    const val OBSERVACION_MIN = 10
    const val COMENTARIO_MIN = 10

    fun puntaje(valor: Int?): ValidationResult = when {
        valor == null -> ValidationResult.Error("Asigna un puntaje")
        valor !in CalculadoraPuntaje.MINIMO..CalculadoraPuntaje.MAXIMO -> ValidationResult.Error("El puntaje va de 1 a 5")
        else -> ValidationResult.Ok
    }

    fun observacion(puntaje: Int?, texto: String): ValidationResult =
        if (puntaje != null && puntaje <= PUNTAJE_BAJO && texto.trim().length < OBSERVACION_MIN) {
            ValidationResult.Error("Justifica el puntaje bajo (mínimo $OBSERVACION_MIN caracteres)")
        } else {
            ValidationResult.Ok
        }

    fun comentario(texto: String): ValidationResult {
        val largo = texto.trim().length
        return when {
            largo == 0 -> ValidationResult.Error("El comentario general es obligatorio")
            largo < COMENTARIO_MIN -> ValidationResult.Error("Mínimo $COMENTARIO_MIN caracteres ($largo/$COMENTARIO_MIN)")
            else -> ValidationResult.Ok
        }
    }
}
