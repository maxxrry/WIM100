package cl.duoc.wim100.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cl.duoc.wim100.model.RegistroHistorial
import cl.duoc.wim100.model.TipoAccion
import cl.duoc.wim100.ui.components.formatearFecha
import cl.duoc.wim100.ui.viewmodel.HistorialUiState

/** Trazabilidad: todas las acciones, de la más reciente a la más antigua. */
@Composable
fun HistorialScreen(estado: HistorialUiState) {
    when {
        estado.cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        estado.registros.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Sin acciones registradas", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        else -> LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
            items(estado.registros, key = { it.id }) { registro ->
                FilaHistorial(registro)
                HorizontalDivider(Modifier.padding(start = 56.dp))
            }
        }
    }
}

@Composable
private fun FilaHistorial(registro: RegistroHistorial) {
    // Los duplicados bloqueados se pintan en rojo para que se noten
    val color = if (registro.tipo == TipoAccion.DUPLICADO_BLOQUEADO) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.primary

    Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.Top) {
        Icon(icono(registro.tipo), contentDescription = null, tint = color)
        Column(
            Modifier.padding(start = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(registro.tipo.etiqueta, style = MaterialTheme.typography.titleSmall, color = color)
            Text(registro.detalle, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${registro.usuario} · ${formatearFecha(registro.fecha)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun icono(tipo: TipoAccion): ImageVector = when (tipo) {
    TipoAccion.INICIO_SESION -> Icons.AutoMirrored.Filled.Login
    TipoAccion.REGISTRO_CANDIDATA -> Icons.Filled.PersonAdd
    TipoAccion.EVALUACION -> Icons.Filled.RateReview
    TipoAccion.DUPLICADO_BLOQUEADO -> Icons.Filled.Block
    TipoAccion.DECISION -> Icons.Filled.Gavel
    TipoAccion.CV_ABIERTO -> Icons.Filled.PictureAsPdf
}
