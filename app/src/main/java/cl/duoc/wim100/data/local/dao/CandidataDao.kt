package cl.duoc.wim100.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.wim100.model.Candidata
import cl.duoc.wim100.model.EstadoCandidata
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidataDao {

    /** Busca por nombre o especialidad. Con texto vacío devuelve todas. */
    @Query(
        """
        SELECT * FROM candidatas
        WHERE nombre LIKE '%' || :texto || '%' OR especialidad LIKE '%' || :texto || '%'
        ORDER BY fechaRegistro DESC
        """
    )
    fun buscar(texto: String): Flow<List<Candidata>>

    @Query("SELECT * FROM candidatas WHERE id = :id")
    fun observar(id: Long): Flow<Candidata?>

    @Query("SELECT * FROM candidatas WHERE id = :id")
    suspend fun obtener(id: Long): Candidata?

    @Insert
    suspend fun insertar(candidata: Candidata): Long

    @Query("UPDATE candidatas SET estado = :estado WHERE id = :id")
    suspend fun cambiarEstado(id: Long, estado: EstadoCandidata)

    @Query("SELECT COUNT(*) FROM candidatas")
    suspend fun contar(): Int
}
