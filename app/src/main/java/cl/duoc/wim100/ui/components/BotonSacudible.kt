package cl.duoc.wim100.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Botón principal de los formularios.
 * Cada vez que [intentosFallidos] aumenta, se sacude de lado a lado para avisar el error.
 * Mientras [cargando] es true muestra un indicador y no se puede presionar.
 */
@Composable
fun BotonSacudible(
    texto: String,
    alPresionar: () -> Unit,
    intentosFallidos: Int,
    modifier: Modifier = Modifier,
    cargando: Boolean = false
) {
    val desplazamiento = remember { Animatable(0f) }

    // Se ejecuta cada vez que cambia el contador (no al mostrarse la primera vez)
    LaunchedEffect(intentosFallidos) {
        if (intentosFallidos == 0) return@LaunchedEffect
        for (x in listOf(-24f, 24f, -16f, 16f, -8f, 0f)) {
            desplazamiento.animateTo(x, tween(durationMillis = 45))
        }
    }

    Button(
        onClick = alPresionar,
        enabled = !cargando,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .graphicsLayer { translationX = desplazamiento.value }
    ) {
        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(texto)
        }
    }
}
