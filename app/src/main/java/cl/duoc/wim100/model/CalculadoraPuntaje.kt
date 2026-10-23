package cl.duoc.wim100.model

import kotlin.math.roundToInt

/** Regla del puntaje: cada criterio vale de 1 a 5 y el total se lleva a escala 0–100. */
object CalculadoraPuntaje {
    const val MINIMO = 1
    const val MAXIMO = 5

    /**
     * Total = suma de puntajes / (cantidad de criterios × 5) × 100.
     * Con 5 criterios equivale a "suma × 4". Un criterio sin puntaje suma 0.
     */
    fun total(puntajes: Map<Criterio, Int>): Int {
        val suma = Criterio.entries.sumOf { puntajes[it] ?: 0 }
        val maximoPosible = Criterio.entries.size * MAXIMO
        return (suma * 100.0 / maximoPosible).roundToInt()
    }
}
