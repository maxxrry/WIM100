package cl.duoc.wim100.ui.screens

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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duoc.wim100.model.Candidata
import cl.duoc.wim100.nativo.ArchivoHelper
import cl.duoc.wim100.ui.components.BotonSacudible
import cl.duoc.wim100.ui.components.CampoTexto
import cl.duoc.wim100.ui.components.MensajeError
import cl.duoc.wim100.ui.viewmodel.Campo
import cl.duoc.wim100.ui.viewmodel.FormularioUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    estado: FormularioUiState,
    alCambiar: (Campo, String) -> Unit,
    alElegirCv: (uri: String, nombre: String) -> Unit,
    alQuitarCv: () -> Unit,
    alGuardar: () -> Unit,
    alVolver: () -> Unit
) {
    // Recurso nativo: selector de documentos del sistema (solo PDF)
    val abrirSelector = ArchivoHelper.rememberSelectorPdf(alElegirCv)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva candidata") },
                navigationIcon = {
                    IconButton(onClick = alVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Función corta para no repetir valor/error/alCambiar en cada campo
            @Composable
            fun CampoFormulario(campo: Campo, etiqueta: String, icono: ImageVector,
                      teclado: KeyboardType = KeyboardType.Text, lineas: Int = 1) = CampoTexto(
                valor = estado.valor(campo),
                alCambiar = { alCambiar(campo, it) },
                etiqueta = etiqueta,
                icono = icono,
                error = estado.errores[campo],
                teclado = teclado,
                lineas = lineas
            )

            CampoFormulario(Campo.NOMBRE, "Nombre y apellido", Icons.Filled.Person)
            CampoFormulario(Campo.CORREO, "Correo (@ejemplo.cl o @test.cl)", Icons.Filled.Email, KeyboardType.Email)
            CampoFormulario(Campo.TELEFONO, "Teléfono (+56 9 …)", Icons.Filled.Phone, KeyboardType.Phone)
            SelectorEspecialidad(
                valor = estado.valor(Campo.ESPECIALIDAD),
                error = estado.errores[Campo.ESPECIALIDAD],
                alElegir = { alCambiar(Campo.ESPECIALIDAD, it) }
            )
            CampoFormulario(Campo.ANIOS, "Años de experiencia", Icons.Filled.WorkHistory, KeyboardType.Number)
            CampoFormulario(Campo.RESUMEN, "Resumen de experiencia", Icons.Filled.Description, lineas = 4)
            CampoFormulario(Campo.PERFIL, "Perfil (https://www.linkedin.com/in/…)", Icons.Filled.Link, KeyboardType.Uri)

            // CV opcional
            if (estado.cvNombre == null) {
                OutlinedButton(onClick = abrirSelector, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.AttachFile, contentDescription = null)
                    Text("Adjuntar CV (PDF, opcional)", Modifier.padding(start = 8.dp))
                }
            } else {
                Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.medium) {
                    Row(Modifier.padding(start = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.PictureAsPdf, contentDescription = null)
                        Text(
                            estado.cvNombre,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )
                        IconButton(onClick = alQuitarCv) {
                            Icon(Icons.Filled.Close, contentDescription = "Quitar CV")
                        }
                    }
                }
            }

            MensajeError(estado.mensajeError)
            BotonSacudible(
                texto = "Guardar candidata",
                alPresionar = alGuardar,
                intentosFallidos = estado.intentosFallidos,
                cargando = estado.guardando
            )
        }
    }
}

/** Menú desplegable de solo lectura: evita que se escriba una especialidad que no existe. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorEspecialidad(valor: String, error: String?, alElegir: (String) -> Unit) {
    var abierto by remember { mutableStateOf(false) }
    Column {
        ExposedDropdownMenuBox(expanded = abierto, onExpandedChange = { abierto = it }) {
            OutlinedTextField(
                value = valor,
                onValueChange = {},
                readOnly = true,
                label = { Text("Especialidad") },
                leadingIcon = { Icon(Icons.Filled.Work, contentDescription = null) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierto) },
                isError = error != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
                Candidata.ESPECIALIDADES.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            alElegir(opcion)
                            abierto = false
                        }
                    )
                }
            }
        }
        MensajeError(error, Modifier.padding(start = 16.dp, top = 4.dp))
    }
}
