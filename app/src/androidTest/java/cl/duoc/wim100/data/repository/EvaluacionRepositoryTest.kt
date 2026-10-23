package cl.duoc.wim100.data.repository

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import cl.duoc.wim100.data.local.WimDatabase
import cl.duoc.wim100.model.Candidata
import cl.duoc.wim100.model.Criterio
import cl.duoc.wim100.model.EstadoCandidata
import cl.duoc.wim100.model.TipoAccion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Prueba la regla de duplicados con una base en memoria (requiere emulador). */
@RunWith(AndroidJUnit4::class)
class EvaluacionRepositoryTest {

    private lateinit var db: WimDatabase
    private lateinit var candidatas: CandidataRepository
    private lateinit var evaluaciones: EvaluacionRepository
    private val todoEnCuatro = Criterio.entries.associateWith { 4 }

    @Before
    fun crear() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, WimDatabase::class.java).build()
        val historial = HistorialRepository(db.historialDao())
        candidatas = CandidataRepository(db.candidataDao(), historial)
        evaluaciones = EvaluacionRepository(db, historial)
    }

    @After
    fun cerrar() = db.close()

    private suspend fun nuevaCandidata() = candidatas.registrar(
        Candidata(
            nombre = "Prueba Test", correo = "prueba@test.cl", telefono = "+56 9 0000 0000",
            especialidad = "Pentesting", aniosExperiencia = 1, resumen = "Resumen de prueba",
            perfilUrl = "https://www.linkedin.com/in/prueba", registradaPor = "Test"
        )
    )

    @Test
    fun segundaEvaluacionMismaEvaluadoraSeBloquea() = runBlocking {
        val id = nuevaCandidata()
        assertEquals(80, evaluaciones.guardar(id, "Paula Ríos", todoEnCuatro, emptyMap(), "Primera"))

        // Mismo nombre con otras mayúsculas y espacios extra
        val bloqueada = runCatching {
            evaluaciones.guardar(id, "  PAULA   ríos ", todoEnCuatro, emptyMap(), "Segunda")
        }
        assertTrue(bloqueada.exceptionOrNull() is EvaluacionDuplicadaException)

        assertEquals(1, evaluaciones.observarDeCandidata(id).first().size)
        assertTrue(db.historialDao().observarTodo().first().any { it.tipo == TipoAccion.DUPLICADO_BLOQUEADO })
    }

    @Test
    fun dosEvaluadorasDistintasDejanLaCandidataEvaluada() = runBlocking {
        val id = nuevaCandidata()
        evaluaciones.guardar(id, "Paula Ríos", todoEnCuatro, emptyMap(), "Uno")
        assertEquals(EstadoCandidata.EN_REVISION, candidatas.observar(id).first()?.estado)
        evaluaciones.guardar(id, "Carla Méndez", todoEnCuatro, emptyMap(), "Dos")
        assertEquals(EstadoCandidata.EVALUADA, candidatas.observar(id).first()?.estado)
    }
}
