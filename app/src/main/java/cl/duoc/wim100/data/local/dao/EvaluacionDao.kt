package cl.duoc.wim100.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.wim100.model.Evaluacion
import cl.duoc.wim100.model.Puntaje
import cl.duoc.wim100.model.ResultadoCandidata
import kotlinx.coroutines.flow.Flow

@Dao
interface EvaluacionDao {

    /** Si ya existe (candidataId, evaluadora), el índice único lanza SQLiteConstraintException. */
    @Insert
    suspend fun insertar(evaluacion: Evaluacion): Long

    @Insert
    suspend fun insertarPuntajes(puntajes: List<Puntaje>)

    /** La columna evaluadora es NOCASE: compara sin distinguir mayúsculas. */
    @Query("SELECT EXISTS(SELECT 1 FROM evaluaciones WHERE candidataId = :candidataId AND evaluadora = :evaluadora)")
    suspend fun existe(candidataId: Long, evaluadora: String): Boolean

    @Query("SELECT COUNT(*) FROM evaluaciones WHERE candidataId = :candidataId")
    suspend fun contar(candidataId: Long): Int

    @Query("SELECT * FROM evaluaciones WHERE candidataId = :candidataId ORDER BY fecha DESC")
    fun observarDeCandidata(candidataId: Long): Flow<List<Evaluacion>>

    /** Todos los puntajes de una candidata; la pantalla los agrupa por evaluacionId. */
    @Query(
        """
        SELECT p.* FROM puntajes p
        INNER JOIN evaluaciones e ON e.id = p.evaluacionId
        WHERE e.candidataId = :candidataId
        """
    )
    fun observarPuntajes(candidataId: Long): Flow<List<Puntaje>>

    /** Ranking: promedio de puntaje por candidata. Las sin evaluar (NULL) quedan al final. */
    @Query(
        """
        SELECT c.id AS candidataId, c.nombre, c.especialidad, c.estado,
               COUNT(e.id) AS evaluaciones, AVG(e.puntajeTotal) AS promedio
        FROM candidatas c
        LEFT JOIN evaluaciones e ON e.candidataId = c.id
        GROUP BY c.id
        ORDER BY promedio DESC, c.nombre
        """
    )
    fun observarResultados(): Flow<List<ResultadoCandidata>>
}
