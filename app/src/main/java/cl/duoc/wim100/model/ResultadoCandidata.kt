package cl.duoc.wim100.model

/** Fila del ranking de Resultados. No es tabla: la arma una consulta GROUP BY. */
data class ResultadoCandidata(
    val candidataId: Long,
    val nombre: String,
    val especialidad: String,
    val estado: EstadoCandidata,
    val evaluaciones: Int,
    val promedio: Double?           // null si nadie la ha evaluado
)
