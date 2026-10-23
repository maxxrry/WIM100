package cl.duoc.wim100.nativo

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import cl.duoc.wim100.R

/**
 * RECURSO NATIVO 2: notificaciones locales.
 * Desde Android 13 (API 33) el permiso POST_NOTIFICATIONS se pide en tiempo de ejecución.
 * Si la usuaria lo rechaza, la app funciona igual: simplemente no se muestra el aviso.
 */
object NotificacionHelper {

    private const val CANAL = "evaluaciones"

    /** Los canales existen desde Android 8 (API 26). Se crea una vez al iniciar la app. */
    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val canal = NotificationChannel(CANAL, "Evaluaciones", NotificationManager.IMPORTANCE_DEFAULT)
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    fun tienePermiso(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED

    /** Muestra la notificación solo si hay permiso. Devuelve true si se mostró. */
    @SuppressLint("MissingPermission")   // el permiso se revisa en tienePermiso()
    fun notificar(context: Context, titulo: String, texto: String): Boolean {
        if (!tienePermiso(context)) return false
        val notificacion = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notificacion)
        return true
    }

    /**
     * Pide el permiso al mostrarse por primera vez (solo Android 13+ y si aún no está concedido).
     * Tras dos rechazos Android deja de mostrar el diálogo por sí solo.
     */
    @Composable
    fun SolicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val context = LocalContext.current
        val lanzador = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            // Concedido o no, no hay nada más que hacer: notificar() revisa el permiso cada vez
        }
        LaunchedEffect(Unit) {
            if (!tienePermiso(context)) lanzador.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
