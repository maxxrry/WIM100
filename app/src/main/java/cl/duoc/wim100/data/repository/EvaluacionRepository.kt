package cl.duoc.wim100.data.repository

import android.database.sqlite.SQLiteConstraintException
import androidx.room.withTransaction
import cl.duoc.wim100.data.local.WimDatabase
import cl.duoc.wim100.model.CalculadoraPuntaje
import cl.duoc.wim100.model.Criterio
import cl.duoc.wim100.model.EstadoCandidata
import cl.duoc.wim100.model.Evaluacion
import cl.duoc.wim100.model.Puntaje
import cl.duoc.wim100.model.ResultadoCandidata
import cl.duoc.wim100.model.TipoAccion
import kotlinx.coroutines.flow.Flow

/** Se lanza cuando una evaluadora intenta evaluar dos veces a la misma candidata. */
class EvaluacionDuplicadaException : Exception("Ya evaluaste a esta candidata")

class EvaluacionRepository(
    private val db: WimDatabase,
    private val historial: HistorialRepository
) {
    private val evaluacionDao = db.evaluacionDao()
    private val candidataDao = db.candidataDao()

    fun observarDeCandidata(candidataId: Long): Flow<List<Evaluacion>> = evaluacionDao.observarDeCandidata(candidataId)

    fun observarPuntajes(candidataId: Long): Flow<List<Puntaje>> = evaluacionDao.observarPuntajes(candidataId)

    fun observarResultados(): Flow<List<ResultadoCandidata>> = evaluacionDao.observarResultados()

    suspend fun yaEvaluo(candidataId: Long, evaluadora: String): Boolean =
        evaluacionDao.existe(candidataId, normalizar(evaluadora))

    /**
     * Guarda la evaluación en una transacción (si algo falla no se guarda nada):
     * revisa duplicado → calcula total → guarda evaluación y puntajes → actualiza estado → historial.
     * Devuelve el puntaje total (0–100).
     */
    suspend fun guardar(
        candidataId: Long,
        evaluadora: String,
        puntajes: Map<Criterio, Int>,
        observaciones: Map<Criterio, String>,
        comentario: String
    ): Int {
        val nombre = normalizar(evaluadora)
        try {
            return db.withTransaction {
                val candidata = candidataDao.obtener(candidataId) ?: error("La candidata no existe")
                check(!candidata.estado.cerrada) { "Dirección ya cerró esta postulación" }
                // Barrera 1: consulta previa
                if (evaluacionDao.existe(candidataId, nombre)) throw EvaluacionDuplicadaException()

                val total = CalculadoraPuntaje.total(puntajes)
                // Barrera 2: si la consulta fallara, el índice único lanza SQLiteConstraintException
                val evaluacionId = evaluacionDao.insertar(
                    Evaluacion(candidataId = candidataId, evaluadora = nombre, puntajeTotal = total, comentario = comentario.trim())
                )
                evaluacionDao.insertarPuntajes(
                    puntajes.map { (criterio, valor) ->
                        Puntaje(evaluacionId, criterio, valor, observaciones[criterio].orEmpty().trim())
                    }
                )

                val estado = if (evaluacionDao.contar(candidataId) >= Evaluacion.REQUERIDAS) {
                    EstadoCandidata.EVALUADA
                } else {
                    EstadoCandidata.EN_REVISION
                }
                candidataDao.cambiarEstado(candidataId, estado)
                historial.registrar(TipoAccion.EVALUACION, "${candidata.nombre}: $total pts", nombre, candidataId)
                total
            }
        } catch (e: Exception) {
            if (e !is EvaluacionDuplicadaException && e !is SQLiteConstraintException) throw e
            // Fuera de la transacción: si no, el rollback también borraría este registro
            historial.registrar(TipoAccion.DUPLICADO_BLOQUEADO, "Segundo intento de evaluación", nombre, candidataId)
            throw EvaluacionDuplicadaException()
        }
    }

    /** "  Paula   Ríos " → "Paula Ríos". Las mayúsculas las resuelve NOCASE en la base. */
    // shortcut: NOCASE de SQLite solo ignora mayúsculas en letras ASCII ("í" ≠ "Í"); basta para el MVP
    private fun normalizar(nombre: String) = nombre.trim().replace(Regex("\\s+"), " ")
}
