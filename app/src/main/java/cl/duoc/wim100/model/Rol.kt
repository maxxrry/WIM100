package cl.duoc.wim100.model

/** Perfiles de usuaria del MVP. */
enum class Rol(val etiqueta: String, val descripcion: String) {
    EVALUADORA("Evaluadora", "Registra candidatas y las evalúa con la matriz."),
    DIRECCION("Dirección", "Revisa resultados y decide quién queda seleccionada.")
}

/** Quién está usando la app. Se guarda en cada acción del historial. */
data class Sesion(val nombre: String, val rol: Rol)
