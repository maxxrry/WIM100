package cl.duoc.wim100.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cl.duoc.wim100.model.CalculadoraPuntaje

/** Fila de 5 botones unidos para elegir el puntaje de un criterio. [valor] null = sin elegir. */
@Composable
fun SelectorPuntaje(valor: Int?, alElegir: (Int) -> Unit, modifier: Modifier = Modifier) {
    val opciones = CalculadoraPuntaje.MINIMO..CalculadoraPuntaje.MAXIMO
    SingleChoiceSegmentedButtonRow(modifier.fillMaxWidth()) {
        opciones.forEachIndexed { indice, puntaje ->
            SegmentedButton(
                selected = valor == puntaje,
                onClick = { alElegir(puntaje) },
                shape = SegmentedButtonDefaults.itemShape(index = indice, count = opciones.count()),
                icon = {}   // sin check: el número seleccionado ya se distingue por el color
            ) {
                Text(puntaje.toString())
            }
        }
    }
}
