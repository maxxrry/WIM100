package cl.duoc.wim100.ui.screens

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.wim100.model.Criterio
import cl.duoc.wim100.ui.components.BotonSacudible
import cl.duoc.wim100.ui.components.CampoTexto
import cl.duoc.wim100.ui.components.MensajeError
import cl.duoc.wim100.ui.components.SelectorPuntaje
import cl.duoc.wim100.ui.viewmodel.EvaluacionUiState
import cl.duoc.wim100.validation.EvaluacionValidator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluacionScreen(
    estado: EvaluacionUiState,
    alElegirPuntaje: (Criterio, Int) -> Unit,
    alCambiarObservacion: (Criterio, String) -> Unit,
    alCambiarComentario: (String) -> Unit,
    alGuardar: () -> Unit,
    alVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Evaluar")
                        Text(estado.nombreCandidata, style = MaterialTheme.typography.bodySmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = { BarraTotal(estado, alGuardar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Asigna de 1 (insuficiente) a 5 (sobresaliente). Con 1 o 2 debes justificar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Criterio.entries.forEach { criterio ->
                TarjetaCriterio(
                    criterio = criterio,
                    puntaje = estado.puntajes[criterio],
                    observacion = estado.observaciones[criterio].orEmpty(),
                    error = estado.erroresCriterio[criterio],
                    alElegir = { alElegirPuntaje(criterio, it) },
                    alCambiarObservacion = { alCambiarObservacion(criterio, it) }
                )
            }
            CampoTexto(
                valor = estado.comentario,
                alCambiar = alCambiarComentario,
                etiqueta = "Comentario general",
                icono = Icons.Filled.Edit,
                error = estado.errorComentario,
                lineas = 3
            )
        }
    }
}

@Composable
private fun TarjetaCriterio(
    criterio: Criterio,
    puntaje: Int?,
    observacion: String,
    error: String?,
    alElegir: (Int) -> Unit,
    alCambiarObservacion: (String) -> Unit
) {
    val obligatoria = puntaje != null && puntaje <= EvaluacionValidator.PUNTAJE_BAJO
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(criterio.titulo, style = MaterialTheme.typography.titleMedium)
            Text(criterio.descripcion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            SelectorPuntaje(valor = puntaje, alElegir = alElegir)
            // Sin puntaje, el error es "asigna un puntaje" y va bajo los botones; si no, es de la observación
            MensajeError(error.takeIf { puntaje == null })
            CampoTexto(
                valor = observacion,
                alCambiar = alCambiarObservacion,
                etiqueta = if (obligatoria) "Observación (obligatoria)" else "Observación (opcional)",
                icono = Icons.Filled.EditNote,
                error = error.takeIf { puntaje != null }
            )
        }
    }
}

/** Barra fija inferior: total en vivo (animado) + mensaje general + botón guardar. */
@Composable
private fun BarraTotal(estado: EvaluacionUiState, alGuardar: () -> Unit) {
    // El número "corre" hasta el valor nuevo en vez de saltar
    val total by animateIntAsState(estado.totalEnVivo, label = "total")
    Surface(tonalElevation = 3.dp) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Total", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                Text("$total / 100", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.secondary)
            }
            LinearProgressIndicator(
                progress = { total / 100f },
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.fillMaxWidth()
            )
            MensajeError(estado.mensajeError)
            BotonSacudible(
                texto = "Guardar evaluación",
                alPresionar = alGuardar,
                intentosFallidos = estado.intentosFallidos,
                cargando = estado.guardando
            )
        }
    }
}
