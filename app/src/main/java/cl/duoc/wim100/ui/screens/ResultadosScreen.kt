package cl.duoc.wim100.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duoc.wim100.model.ResultadoCandidata
import cl.duoc.wim100.ui.components.ChipEstado
import cl.duoc.wim100.ui.viewmodel.ResultadosUiState
import kotlin.math.roundToInt

private const val PODIO = 3

/** Ranking consolidado. Viene ordenado por promedio desde la consulta de Room. */
@Composable
fun ResultadosScreen(estado: ResultadosUiState, alAbrir: (Long) -> Unit) {
    if (estado.cargando) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                "Ranking por promedio de evaluaciones",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        itemsIndexed(estado.ranking, key = { _, fila -> fila.candidataId }) { indice, fila ->
            FilaRanking(
                posicion = indice + 1,
                fila = fila,
                alTocar = { alAbrir(fila.candidataId) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@Composable
private fun FilaRanking(posicion: Int, fila: ResultadoCandidata, alTocar: () -> Unit, modifier: Modifier = Modifier) {
    val colores = MaterialTheme.colorScheme
    val promedio = fila.promedio
    val enPodio = promedio != null && posicion <= PODIO

    Card(onClick = alTocar, modifier = modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Posición: ámbar para el podio, gris para el resto, "–" si no tiene evaluaciones
            Surface(
                shape = CircleShape,
                color = if (enPodio) colores.secondary else colores.surfaceVariant,
                contentColor = if (enPodio) colores.onSecondary else colores.onSurfaceVariant,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(if (promedio == null) "–" else "$posicion", style = MaterialTheme.typography.titleMedium)
                }
            }

            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        fila.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    ChipEstado(fila.estado)
                }
                Text(
                    "${fila.especialidad} · ${fila.evaluaciones} ${if (fila.evaluaciones == 1) "evaluación" else "evaluaciones"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colores.onSurfaceVariant
                )
                if (promedio == null) {
                    Text("Sin evaluaciones", style = MaterialTheme.typography.bodySmall, color = colores.onSurfaceVariant)
                } else {
                    BarraPromedio(promedio)
                }
            }
        }
    }
}

/** Barra que crece desde 0 hasta el promedio la primera vez que aparece. */
@Composable
private fun BarraPromedio(promedio: Double) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val progreso by animateFloatAsState(
        targetValue = if (visible) (promedio / 100).toFloat() else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "promedio"
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        LinearProgressIndicator(
            progress = { progreso },
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            "${promedio.roundToInt()}",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
