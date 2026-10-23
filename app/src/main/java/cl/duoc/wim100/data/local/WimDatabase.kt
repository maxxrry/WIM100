package cl.duoc.wim100.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.wim100.data.local.dao.CandidataDao
import cl.duoc.wim100.data.local.dao.EvaluacionDao
import cl.duoc.wim100.data.local.dao.HistorialDao
import cl.duoc.wim100.model.Candidata
import cl.duoc.wim100.model.Evaluacion
import cl.duoc.wim100.model.Puntaje
import cl.duoc.wim100.model.RegistroHistorial

/** Base de datos SQLite de la app (archivo wim100.db). Los enums se guardan como texto. */
@Database(
    entities = [Candidata::class, Evaluacion::class, Puntaje::class, RegistroHistorial::class],
    version = 1,
    exportSchema = false
)
abstract class WimDatabase : RoomDatabase() {

    abstract fun candidataDao(): CandidataDao
    abstract fun evaluacionDao(): EvaluacionDao
    abstract fun historialDao(): HistorialDao

    companion object {
        @Volatile
        private var instancia: WimDatabase? = null

        /** Una sola instancia para toda la app (singleton). */
        fun obtener(context: Context): WimDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(context.applicationContext, WimDatabase::class.java, "wim100.db")
                    // MVP: si cambia el esquema se borra y recrea (no hay datos reales que perder)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { instancia = it }
            }
    }
}
