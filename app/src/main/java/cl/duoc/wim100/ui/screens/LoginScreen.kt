package cl.duoc.wim100.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duoc.wim100.model.Rol
import cl.duoc.wim100.ui.components.BotonSacudible
import cl.duoc.wim100.ui.components.CampoTexto
import cl.duoc.wim100.ui.components.MensajeError
import cl.duoc.wim100.ui.viewmodel.LoginUiState

/** Pantalla de ingreso: nombre + rol. No guarda estado propio, todo viene del SesionViewModel. */
@Composable
fun LoginScreen(
    estado: LoginUiState,
    alCambiarNombre: (String) -> Unit,
    alElegirRol: (Rol) -> Unit,
    alIngresar: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Surface(
            shape = CircleShape,
            color = colores.primaryContainer,
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Icon(Icons.Filled.Shield, contentDescription = null, tint = colores.primary, modifier = Modifier.padding(18.dp))
        }
        Text(
            "WIM 100",
            style = MaterialTheme.typography.headlineMedium,
            color = colores.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "Postulación y evaluación de candidatas · DYM Cyber",
            style = MaterialTheme.typography.bodyMedium,
            color = colores.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        CampoTexto(
            valor = estado.nombre,
            alCambiar = alCambiarNombre,
            etiqueta = "Tu nombre",
            icono = Icons.Filled.Person,
            error = estado.errorNombre
        )

        Text("¿Con qué rol ingresas?", style = MaterialTheme.typography.titleMedium)
        // selectableGroup: los lectores de pantalla lo anuncian como un grupo de opciones
        Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Rol.entries.forEach { rol ->
                OpcionRol(rol, seleccionado = estado.rol == rol, alElegir = { alElegirRol(rol) })
            }
        }
        MensajeError(estado.errorRol, Modifier.padding(start = 16.dp))

        Spacer(Modifier.height(8.dp))
        BotonSacudible(texto = "Ingresar", alPresionar = alIngresar, intentosFallidos = estado.intentosFallidos)
    }
}

/** Tarjeta seleccionable con un RadioButton; toda la tarjeta responde al toque. */
@Composable
private fun OpcionRol(rol: Rol, seleccionado: Boolean, alElegir: () -> Unit) {
    val colores = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(if (seleccionado) 2.dp else 1.dp, if (seleccionado) colores.primary else colores.outlineVariant),
        color = if (seleccionado) colores.primaryContainer else colores.surface,
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = seleccionado, onClick = alElegir, role = Role.RadioButton)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = seleccionado, onClick = null)
            Column(Modifier.padding(start = 8.dp)) {
                Text(rol.etiqueta, style = MaterialTheme.typography.titleSmall)
                Text(rol.descripcion, style = MaterialTheme.typography.bodySmall, color = colores.onSurfaceVariant)
            }
        }
    }
}
