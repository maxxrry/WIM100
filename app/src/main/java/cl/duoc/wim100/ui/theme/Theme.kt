package cl.duoc.wim100.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaClaro = lightColorScheme(
    primary = AzulNoche40,
    onPrimary = FondoClaro,
    primaryContainer = AzulNoche90,
    onPrimaryContainer = AzulNoche10,
    secondary = Ambar40,
    onSecondary = FondoClaro,
    secondaryContainer = Ambar90,
    onSecondaryContainer = Ambar10,
    tertiary = Acero40,
    tertiaryContainer = Acero90,
    onTertiaryContainer = Acero10,
    background = FondoClaro,
    onBackground = TextoClaro,
    surface = FondoClaro,
    onSurface = TextoClaro
)

private val EsquemaOscuro = darkColorScheme(
    primary = AzulNoche80,
    onPrimary = AzulNoche20,
    primaryContainer = AzulNoche30,
    onPrimaryContainer = AzulNoche90,
    secondary = Ambar80,
    onSecondary = Ambar20,
    secondaryContainer = Ambar30,
    onSecondaryContainer = Ambar90,
    tertiary = Acero80,
    tertiaryContainer = Acero40,
    onTertiaryContainer = Acero90,
    background = FondoNoche,
    onBackground = TextoNoche,
    surface = FondoNoche,
    onSurface = TextoNoche,
    surfaceContainer = SuperficieNoche
)

/**
 * Tema de la app. Sin color dinámico (Android 12+) a propósito:
 * así siempre se ve la paleta azul noche + ámbar y no los colores del fondo de pantalla.
 */
@Composable
fun WIM100Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        content = content
    )
}
