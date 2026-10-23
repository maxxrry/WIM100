package cl.duoc.wim100.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.wim100.model.EstadoCandidata

/** Etiqueta con el estado de la candidata. Cada estado usa un color del tema. */
@Composable
fun ChipEstado(estado: EstadoCandidata, modifier: Modifier = Modifier) {
    val colores = MaterialTheme.colorScheme
    val (fondo, texto) = when (estado) {
        EstadoCandidata.RECIBIDA -> colores.surfaceVariant to colores.onSurfaceVariant
        EstadoCandidata.EN_REVISION -> colores.tertiaryContainer to colores.onTertiaryContainer
        EstadoCandidata.EVALUADA -> colores.primaryContainer to colores.onPrimaryContainer
        EstadoCandidata.SELECCIONADA -> colores.secondaryContainer to colores.onSecondaryContainer   // ámbar
        EstadoCandidata.NO_SELECCIONADA -> colores.errorContainer to colores.onErrorContainer
    }
    Surface(color = fondo, contentColor = texto, shape = RoundedCornerShape(50), modifier = modifier) {
        Text(
            estado.etiqueta,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
