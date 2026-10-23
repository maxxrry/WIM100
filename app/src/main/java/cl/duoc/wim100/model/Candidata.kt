package cl.duoc.wim100.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Postulante al programa WIM 100. Solo datos ficticios. */
@Entity(tableName = "candidatas")
data class Candidata(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val correo: String,
    val telefono: String,
    val especialidad: String,
    val aniosExperiencia: Int,
    val resumen: String,
    val perfilUrl: String,            // enlace linkedin simulado
    val cvUri: String? = null,        // PDF elegido con el selector del sistema
    val cvNombre: String? = null,
    val estado: EstadoCandidata = EstadoCandidata.RECIBIDA,
    val registradaPor: String,
    val fechaRegistro: Long = System.currentTimeMillis()
) {
    companion object {
        val ESPECIALIDADES = listOf(
            "Pentesting",
            "Operaciones SOC",
            "Cumplimiento normativo",
            "Seguridad en la nube",
            "Análisis de malware",
            "Concientización y formación"
        )
    }
}
