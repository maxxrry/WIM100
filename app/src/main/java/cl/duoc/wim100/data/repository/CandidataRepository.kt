package cl.duoc.wim100.data.repository

import cl.duoc.wim100.data.local.dao.CandidataDao
import cl.duoc.wim100.model.Candidata
import cl.duoc.wim100.model.EstadoCandidata
import cl.duoc.wim100.model.TipoAccion
import kotlinx.coroutines.flow.Flow

class CandidataRepository(
    private val dao: CandidataDao,
    private val historial: HistorialRepository
) {
    fun buscar(texto: String): Flow<List<Candidata>> = dao.buscar(texto.trim())

    fun observar(id: Long): Flow<Candidata?> = dao.observar(id)

    /** Guarda la candidata y deja registro en el historial. Devuelve el id nuevo. */
    suspend fun registrar(candidata: Candidata): Long {
        val id = dao.insertar(candidata)
        historial.registrar(TipoAccion.REGISTRO_CANDIDATA, candidata.nombre, candidata.registradaPor, id)
        return id
    }

    /** Dirección cierra la postulación como Seleccionada o No seleccionada. */
    suspend fun decidir(id: Long, decision: EstadoCandidata, usuario: String) {
        require(decision.cerrada) { "La decisión debe ser Seleccionada o No seleccionada" }
        val candidata = dao.obtener(id) ?: return
        dao.cambiarEstado(id, decision)
        historial.registrar(TipoAccion.DECISION, "${candidata.nombre}: ${decision.etiqueta}", usuario, id)
    }

    suspend fun registrarCvAbierto(candidata: Candidata, usuario: String) =
        historial.registrar(TipoAccion.CV_ABIERTO, candidata.cvNombre ?: candidata.nombre, usuario, candidata.id)
}
