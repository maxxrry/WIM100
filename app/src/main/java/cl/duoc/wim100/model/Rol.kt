package cl.duoc.wim100.model

/** Perfiles de usuaria del MVP. */
enum class Rol(val etiqueta: String) {
    EVALUADORA("Evaluadora"),   // registra y evalúa candidatas
    DIRECCION("Dirección")      // revisa resultados y decide
}

/** Quién está usando la app. Se guarda en cada acción del historial. */
data class Sesion(val nombre: String, val rol: Rol)
