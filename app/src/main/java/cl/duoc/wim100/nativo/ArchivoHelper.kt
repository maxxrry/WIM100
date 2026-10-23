package cl.duoc.wim100.nativo

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

/**
 * RECURSO NATIVO 1: selector de documentos (Storage Access Framework) + abrir PDF con otra app.
 * No requiere permisos de almacenamiento: la usuaria elige el archivo y la app solo accede a ese.
 */
object ArchivoHelper {

    private const val TIPO_PDF = "application/pdf"

    /**
     * Devuelve una función que abre el selector de PDF del sistema.
     * Al elegir un archivo llama a [alElegir] con su URI (como texto) y su nombre.
     */
    @Composable
    fun rememberSelectorPdf(alElegir: (uri: String, nombre: String) -> Unit): () -> Unit {
        val context = LocalContext.current
        val lanzador = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {   // null = la usuaria canceló
                conservarAcceso(context, uri)
                alElegir(uri.toString(), nombreDe(context, uri))
            }
        }
        return { lanzador.launch(arrayOf(TIPO_PDF)) }
    }

    /** Abre el PDF con la app que tenga el teléfono. Devuelve false si no se pudo. */
    fun abrirPdf(context: Context, uri: String): Boolean {
        val intent = Intent(Intent.ACTION_VIEW)
            .setDataAndType(uri.toUri(), TIPO_PDF)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)   // presta el acceso a la otra app
        return try {
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false   // no hay lector de PDF instalado
        } catch (e: SecurityException) {
            false   // el archivo ya no es accesible (borrado o permiso revocado)
        }
    }

    /** Mantiene el permiso de lectura aunque se cierre la app. */
    private fun conservarAcceso(context: Context, uri: Uri) {
        try {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (e: SecurityException) {
            // Algunos proveedores no lo permiten: el acceso dura mientras la app esté abierta
        }
    }

    private fun nombreDe(context: Context, uri: Uri): String =
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        } ?: "cv.pdf"
}
