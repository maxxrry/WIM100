package cl.duoc.wim100.data.local

import androidx.room.withTransaction
import cl.duoc.wim100.model.CalculadoraPuntaje
import cl.duoc.wim100.model.Candidata
import cl.duoc.wim100.model.Criterio
import cl.duoc.wim100.model.EstadoCandidata
import cl.duoc.wim100.model.Evaluacion
import cl.duoc.wim100.model.Puntaje
import cl.duoc.wim100.model.RegistroHistorial
import cl.duoc.wim100.model.TipoAccion

/**
 * Datos de prueba 100 % ficticios para la demo. Solo se cargan si la base está vacía.
 * Para probar el bloqueo de duplicados: entrar como "Paula Ríos" y evaluar a Antonia Muñoz.
 */
object DatosSemilla {

    private const val SISTEMA = "Sistema (datos de prueba)"
    private const val PAULA = "Paula Ríos"
    private const val CARLA = "Carla Méndez"

    private val candidatas = listOf(
        Candidata(
            nombre = "Catalina Fuentes Araya", correo = "catalina.fuentes@test.cl", telefono = "+56 9 4321 1001",
            especialidad = "Pentesting", aniosExperiencia = 8,
            resumen = "Lidera pruebas de intrusión en una empresa ficticia de retail y capacita a su equipo en hacking ético.",
            perfilUrl = "https://www.linkedin.com/in/catalina-fuentes-ficticio",
            estado = EstadoCandidata.EVALUADA, registradaPor = SISTEMA
        ),
        Candidata(
            nombre = "Valentina Rojas Medina", correo = "valentina.rojas@test.cl", telefono = "+56 9 4321 1002",
            especialidad = "Operaciones SOC", aniosExperiencia = 6,
            resumen = "Coordina turnos de monitoreo en un SOC simulado y creó alertas que redujeron falsos positivos.",
            perfilUrl = "https://www.linkedin.com/in/valentina-rojas-ficticio",
            estado = EstadoCandidata.EVALUADA, registradaPor = SISTEMA
        ),
        Candidata(
            nombre = "Antonia Muñoz Cárdenas", correo = "antonia.munoz@test.cl", telefono = "+56 9 4321 1003",
            especialidad = "Cumplimiento normativo", aniosExperiencia = 10,
            resumen = "Implementó la Ley Marco de Ciberseguridad en una institución ficticia y dicta talleres a pymes.",
            perfilUrl = "https://www.linkedin.com/in/antonia-munoz-ficticio",
            estado = EstadoCandidata.EN_REVISION, registradaPor = SISTEMA
        ),
        Candidata(
            nombre = "Sofía Espinoza Vera", correo = "sofia.espinoza@test.cl", telefono = "+56 9 4321 1004",
            especialidad = "Seguridad en la nube", aniosExperiencia = 4,
            resumen = "Configura controles de acceso en nubes públicas para una startup ficticia del área salud.",
            perfilUrl = "https://www.linkedin.com/in/sofia-espinoza-ficticio",
            registradaPor = SISTEMA
        ),
        Candidata(
            nombre = "Camila Torres Pizarro", correo = "camila.torres@test.cl", telefono = "+56 9 4321 1005",
            especialidad = "Análisis de malware", aniosExperiencia = 3,
            resumen = "Analiza muestras de malware en un laboratorio universitario ficticio y publica guías para estudiantes.",
            perfilUrl = "https://www.linkedin.com/in/camila-torres-ficticio",
            registradaPor = SISTEMA
        )
    )

    suspend fun poblarSiVacia(db: WimDatabase) {
        if (db.candidataDao().contar() > 0) return
        db.withTransaction {
            val ids = candidatas.map { candidata ->
                val id = db.candidataDao().insertar(candidata)
                db.historialDao().insertar(
                    RegistroHistorial(
                        tipo = TipoAccion.REGISTRO_CANDIDATA, detalle = candidata.nombre,
                        usuario = SISTEMA, candidataId = id
                    )
                )
                id
            }
            // Los estados de arriba calzan con estas evaluaciones (2 = Evaluada, 1 = En revisión)
            evaluar(db, ids[0], PAULA, listOf(5, 5, 4, 4, 5), "Perfil técnico muy sólido y con equipo a cargo.")
            evaluar(db, ids[0], CARLA, listOf(4, 5, 5, 4, 4), "Gran capacidad para formar a otras personas.")
            evaluar(db, ids[1], PAULA, listOf(4, 4, 5, 3, 4), "Buenos resultados medibles en su SOC.")
            evaluar(db, ids[1], CARLA, listOf(3, 4, 4, 3, 5), "Muy motivada; le falta presencia en comunidades.")
            evaluar(db, ids[2], PAULA, listOf(5, 4, 4, 5, 3), "Amplia trayectoria normativa y mentora activa.")
        }
    }

    private suspend fun evaluar(db: WimDatabase, candidataId: Long, evaluadora: String, valores: List<Int>, comentario: String) {
        val puntajes = Criterio.entries.zip(valores).toMap()
        val evaluacionId = db.evaluacionDao().insertar(
            Evaluacion(
                candidataId = candidataId, evaluadora = evaluadora,
                puntajeTotal = CalculadoraPuntaje.total(puntajes), comentario = comentario
            )
        )
        db.evaluacionDao().insertarPuntajes(puntajes.map { (criterio, valor) -> Puntaje(evaluacionId, criterio, valor) })
    }
}
