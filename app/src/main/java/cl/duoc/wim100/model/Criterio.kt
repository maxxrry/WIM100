package cl.duoc.wim100.model

/**
 * Criterios de la matriz WIM 100. Todos pesan lo mismo, por eso basta un enum
 * (no necesitan tabla propia). Matriz provisional, datos ficticios.
 */
enum class Criterio(val titulo: String, val descripcion: String) {
    FORMACION("Formación y certificaciones", "Estudios, cursos y certificaciones en ciberseguridad."),
    EXPERIENCIA("Experiencia práctica", "Proyectos, auditorías o incidentes en los que ha participado."),
    IMPACTO("Impacto en su organización", "Resultados concretos que logró en su equipo o empresa."),
    MENTORIA("Mentoría y redes", "Apoyo a otras mujeres y participación en comunidades del área."),
    PROYECCION("Proyección y motivación", "Metas claras y potencial de crecimiento dentro del programa.")
}
