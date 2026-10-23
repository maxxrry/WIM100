package cl.duoc.wim100.model

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculadoraPuntajeTest {

    private fun todos(valor: Int) = Criterio.entries.associateWith { valor }

    @Test
    fun todoEnCincoDa100() = assertEquals(100, CalculadoraPuntaje.total(todos(5)))

    @Test
    fun todoEnUnoDa20() = assertEquals(20, CalculadoraPuntaje.total(todos(1)))

    @Test
    fun puntajesMixtosSumanPorCuatro() {
        // 5 + 4 + 3 + 2 + 1 = 15 → 15 × 4 = 60
        val puntajes = Criterio.entries.zip(listOf(5, 4, 3, 2, 1)).toMap()
        assertEquals(60, CalculadoraPuntaje.total(puntajes))
    }

    @Test
    fun sinPuntajesDaCero() = assertEquals(0, CalculadoraPuntaje.total(emptyMap()))
}
