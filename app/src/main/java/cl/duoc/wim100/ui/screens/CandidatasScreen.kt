package cl.duoc.wim100.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duoc.wim100.model.Candidata
import cl.duoc.wim100.ui.components.ChipEstado
import cl.duoc.wim100.ui.viewmodel.CandidatasUiState

/** Lista de candidatas con búsqueda. [puedeRegistrar] = true solo para el rol Evaluadora. */
@Composable
fun CandidatasScreen(
    estado: CandidatasUiState,
    puedeRegistrar: Boolean,
    alBuscar: (String) -> Unit,
    alAbrir: (Long) -> Unit,
    alRegistrar: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = estado.busqueda,
                onValueChange = alBuscar,
                placeholder = { Text("Buscar por nombre o especialidad") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (estado.busqueda.isNotEmpty()) {
                        IconButton(onClick = { alBuscar("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            when {
                estado.cargando -> Centrado { CircularProgressIndicator() }
                estado.candidatas.isEmpty() -> Centrado {
                    Text(
                        if (estado.busqueda.isBlank()) "Aún no hay candidatas registradas"
                        else "Sin resultados para \"${estado.busqueda}\"",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
                else -> LazyColumn(
                    // Espacio abajo para que el botón flotante no tape la última tarjeta
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(estado.candidatas, key = { it.id }) { candidata ->
                        TarjetaCandidata(
                            candidata,
                            alTocar = { alAbrir(candidata.id) },
                            modifier = Modifier.animateItem()   // se reacomoda animada al filtrar
                        )
                    }
                }
            }
        }

        if (puedeRegistrar) {
            ExtendedFloatingActionButton(
                onClick = alRegistrar,
                icon = { Icon(Icons.Filled.PersonAdd, contentDescription = null) },
                text = { Text("Nueva candidata") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun TarjetaCandidata(candidata: Candidata, alTocar: () -> Unit, modifier: Modifier = Modifier) {
    Card(onClick = alTocar, modifier = modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(44.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text(iniciales(candidata.nombre), style = MaterialTheme.typography.titleMedium)
                }
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(candidata.nombre, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    "${candidata.especialidad} · ${candidata.aniosExperiencia} años",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            ChipEstado(candidata.estado)
        }
    }
}

/** "Catalina Fuentes Araya" → "CF" */
private fun iniciales(nombre: String): String =
    nombre.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }

@Composable
private fun Centrado(contenido: @Composable () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) { contenido() }
}
