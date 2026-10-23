package cl.duoc.wim100.navigation

/** Nombres de las rutas del NavHost. Las que llevan {candidataId} reciben el id en la URL. */
object Rutas {
    const val ARG_ID = "candidataId"

    const val LOGIN = "login"
    const val CANDIDATAS = "candidatas"
    const val FORMULARIO = "formulario"
    const val DETALLE = "detalle/{$ARG_ID}"
    const val EVALUACION = "evaluacion/{$ARG_ID}"
    const val RESULTADOS = "resultados"
    const val HISTORIAL = "historial"

    fun detalle(id: Long) = "detalle/$id"
    fun evaluacion(id: Long) = "evaluacion/$id"
}
