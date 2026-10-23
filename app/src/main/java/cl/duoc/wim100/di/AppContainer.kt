package cl.duoc.wim100.di

import android.content.Context
import cl.duoc.wim100.data.local.WimDatabase
import cl.duoc.wim100.data.repository.CandidataRepository
import cl.duoc.wim100.data.repository.EvaluacionRepository
import cl.duoc.wim100.data.repository.HistorialRepository

/** Inyección de dependencias manual: crea una sola instancia de la BD y de cada repositorio. */
class AppContainer(context: Context) {
    val db = WimDatabase.obtener(context)

    val historialRepository = HistorialRepository(db.historialDao())
    val candidataRepository = CandidataRepository(db.candidataDao(), historialRepository)
    val evaluacionRepository = EvaluacionRepository(db, historialRepository)
}
