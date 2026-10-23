package cl.duoc.wim100.model

/** Etapas de una postulación, en orden. */
enum class EstadoCandidata(val etiqueta: String) {
    RECIBIDA("Recibida"),
    EN_REVISION("En revisión"),          // tiene al menos una evaluación
    EVALUADA("Evaluada"),                // alcanzó las evaluaciones requeridas
    SELECCIONADA("Seleccionada"),
    NO_SELECCIONADA("No seleccionada");

    /** Dirección ya decidió: no se aceptan más evaluaciones. */
    val cerrada: Boolean get() = this == SELECCIONADA || this == NO_SELECCIONADA
}
