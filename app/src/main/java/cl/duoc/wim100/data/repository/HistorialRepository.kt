package cl.duoc.wim100.data.repository

import cl.duoc.wim100.data.local.dao.HistorialDao
import cl.duoc.wim100.model.RegistroHistorial
import cl.duoc.wim100.model.TipoAccion
import kotlinx.coroutines.flow.Flow

/** Único punto para dejar y consultar la trazabilidad de acciones. */
class HistorialRepository(private val dao: HistorialDao) {

    fun observarTodo(): Flow<List<RegistroHistorial>> = dao.observarTodo()

    suspend fun registrar(tipo: TipoAccion, detalle: String, usuario: String, candidataId: Long? = null) =
        dao.insertar(RegistroHistorial(tipo = tipo, detalle = detalle, usuario = usuario, candidataId = candidataId))
}
