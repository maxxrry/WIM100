package cl.duoc.wim100.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.wim100.model.RegistroHistorial
import kotlinx.coroutines.flow.Flow

@Dao
interface HistorialDao {

    @Insert
    suspend fun insertar(registro: RegistroHistorial)

    @Query("SELECT * FROM historial ORDER BY fecha DESC, id DESC")
    fun observarTodo(): Flow<List<RegistroHistorial>>
}
