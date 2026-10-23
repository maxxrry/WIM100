package cl.duoc.wim100.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Campo de formulario con ícono. Si [error] no es null, el borde se pone rojo
 * y el mensaje aparece animado debajo.
 */
@Composable
fun CampoTexto(
    valor: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    error: String?,
    modifier: Modifier = Modifier,
    teclado: KeyboardType = KeyboardType.Text,
    lineas: Int = 1
) {
    Column(modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = valor,
            onValueChange = alCambiar,
            label = { Text(etiqueta) },
            leadingIcon = { Icon(icono, contentDescription = null) },
            trailingIcon = if (error != null) {
                { Icon(Icons.Filled.ErrorOutline, contentDescription = "Error") }
            } else null,
            isError = error != null,
            singleLine = lineas == 1,
            minLines = lineas,
            keyboardOptions = KeyboardOptions(keyboardType = teclado),
            modifier = Modifier.fillMaxWidth()
        )
        MensajeError(error, Modifier.padding(start = 16.dp, top = 4.dp))
    }
}

/** Texto de error que entra y sale con animación (crece + aparece / se encoge + desvanece). */
@Composable
fun MensajeError(mensaje: String?, modifier: Modifier = Modifier) {
    // Guarda el último mensaje para que el texto no quede vacío mientras se anima la salida
    val ultimo = remember { mutableStateOf("") }
    if (mensaje != null) ultimo.value = mensaje

    AnimatedVisibility(
        visible = mensaje != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Text(ultimo.value, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
    }
}
