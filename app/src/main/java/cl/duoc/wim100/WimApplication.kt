package cl.duoc.wim100

import android.app.Application
import cl.duoc.wim100.data.local.DatosSemilla
import cl.duoc.wim100.di.AppContainer
import cl.duoc.wim100.nativo.NotificacionHelper
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/** Se crea antes que cualquier pantalla y vive mientras viva la app. */
class WimApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificacionHelper.crearCanal(this)
        // Room ejecuta sus consultas en un hilo propio, así que no bloquea la pantalla
        MainScope().launch { DatosSemilla.poblarSiVacia(container.db) }
    }
}
