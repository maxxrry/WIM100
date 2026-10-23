package cl.duoc.wim100.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Una acción realizada en la app (quién, qué y cuándo).
 * Sin clave foránea a candidatas: el historial se conserva aunque se borre la candidata.
 */
@Entity(tableName = "historial")
data class RegistroHistorial(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tipo: TipoAccion,
    val detalle: String,
    val usuario: String,
    val candidataId: Long? = null,
    val fecha: Long = System.currentTimeMillis()
)

enum class TipoAccion(val etiqueta: String) {
    INICIO_SESION("Inicio de sesión"),
    REGISTRO_CANDIDATA("Candidata registrada"),
    EVALUACION("Evaluación guardada"),
    DUPLICADO_BLOQUEADO("Evaluación duplicada bloqueada"),
    DECISION("Decisión de Dirección"),
    CV_ABIERTO("CV abierto")
}
