package cl.duoc.wim100.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import cl.duoc.wim100.model.Candidata
import cl.duoc.wim100.model.EstadoCandidata
import cl.duoc.wim100.model.Evaluacion
import cl.duoc.wim100.model.Rol
import cl.duoc.wim100.nativo.ArchivoHelper
import cl.duoc.wim100.ui.components.ChipEstado
import cl.duoc.wim100.ui.components.MensajeError
import cl.duoc.wim100.ui.components.formatearFecha
import cl.duoc.wim100.ui.viewmodel.DetalleUiState
import cl.duoc.wim100.ui.viewmodel.EvaluacionDetalle
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    estado: DetalleUiState,
    rol: Rol?,
    alVolver: () -> Unit,
    alEvaluar: () -> Unit,
    alCvAbierto: () -> Unit,
    alDecidir: (EstadoCandidata) -> Unit
) {
    val candidata = estado.candidata

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de candidata") },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (candidata == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Encabezado(candidata, estado.promedio, estado.evaluaciones.size)
            Antecedentes(candidata, alCvAbierto)
            Acciones(candidata.estado, rol, alEvaluar, alDecidir)

            Text("Evaluaciones", style = MaterialTheme.typography.titleLarge)
            if (estado.evaluaciones.isEmpty()) {
                Text("Todavía nadie la ha evaluado.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            estado.evaluaciones.forEach { TarjetaEvaluacion(it) }
        }
    }
}

@Composable
private fun Encabezado(candidata: Candidata, promedio: Double?, cantidad: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(candidata.nombre, style = MaterialTheme.typography.headlineSmall)
        Text(
            "${candidata.especialidad} · ${candidata.aniosExperiencia} años de experiencia",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ChipEstado(candidata.estado)
            if (promedio != null) {
                Text(
                    "${promedio.roundToInt()} / 100 · $cantidad ${if (cantidad == 1) "evaluación" else "evaluaciones"}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun Antecedentes(candidata: Candidata, alCvAbierto: () -> Unit) {
    val context = LocalContext.current
    var errorCv by remember { mutableStateOf<String?>(null) }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Dato(Icons.Filled.Email, candidata.correo)
            Dato(Icons.Filled.Phone, candidata.telefono)
            Dato(Icons.Filled.Link, candidata.perfilUrl)
            HorizontalDivider()
            Text(candidata.resumen, style = MaterialTheme.typography.bodyMedium)

            val cvUri = candidata.cvUri
            if (cvUri == null) {
                Text("Sin CV adjunto", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                OutlinedButton(
                    onClick = {
                        // Recurso nativo: intent ACTION_VIEW hacia la app de PDF del teléfono
                        if (ArchivoHelper.abrirPdf(context, cvUri)) {
                            errorCv = null
                            alCvAbierto()
                        } else {
                            errorCv = "No se pudo abrir el CV: instala un lector de PDF o vuelve a adjuntarlo"
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.PictureAsPdf, contentDescription = null)
                    Text("Abrir CV (${candidata.cvNombre})", Modifier.padding(start = 8.dp))
                }
                MensajeError(errorCv)
            }
        }
    }
}

@Composable
private fun Dato(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(texto, Modifier.padding(start = 12.dp), style = MaterialTheme.typography.bodyMedium)
    }
}

/** Botones según el rol: Evaluadora evalúa; Dirección decide (solo si está Evaluada). */
@Composable
private fun Acciones(estado: EstadoCandidata, rol: Rol?, alEvaluar: () -> Unit, alDecidir: (EstadoCandidata) -> Unit) {
    // Decisión esperando confirmación (null = sin diálogo)
    var porConfirmar by remember { mutableStateOf<EstadoCandidata?>(null) }

    when {
        rol == Rol.EVALUADORA && !estado.cerrada -> Button(onClick = alEvaluar, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.RateReview, contentDescription = null)
            Text("Evaluar", Modifier.padding(start = 8.dp))
        }
        rol == Rol.DIRECCION && estado == EstadoCandidata.EVALUADA -> Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { porConfirmar = EstadoCandidata.SELECCIONADA },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier.weight(1f)
            ) { Text("Seleccionar") }
            OutlinedButton(onClick = { porConfirmar = EstadoCandidata.NO_SELECCIONADA }, modifier = Modifier.weight(1f)) {
                Text("No seleccionar")
            }
        }
        rol == Rol.DIRECCION && !estado.cerrada -> Text(
            "Podrás decidir cuando tenga ${Evaluacion.REQUERIDAS} evaluaciones.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    porConfirmar?.let { decision ->
        AlertDialog(
            onDismissRequest = { porConfirmar = null },
            title = { Text("¿Marcar como ${decision.etiqueta}?") },
            text = { Text("Esta decisión cierra la postulación y no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    alDecidir(decision)
                    porConfirmar = null
                }) { Text("Confirmar") }
            },
            dismissButton = { TextButton(onClick = { porConfirmar = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun TarjetaEvaluacion(detalle: EvaluacionDetalle) {
    val evaluacion = detalle.evaluacion
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(evaluacion.evaluadora, style = MaterialTheme.typography.titleMedium)
                    Text(formatearFecha(evaluacion.fecha), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("${evaluacion.puntajeTotal} pts", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.secondary)
            }
            HorizontalDivider()
            detalle.puntajes.forEach { puntaje ->
                Row {
                    Text(puntaje.criterio.titulo, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Text("${puntaje.valor}/5", style = MaterialTheme.typography.labelLarge)
                }
                if (puntaje.observacion.isNotBlank()) {
                    Text(puntaje.observacion, style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic)
                }
            }
            HorizontalDivider()
            Text("“${evaluacion.comentario}”", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
