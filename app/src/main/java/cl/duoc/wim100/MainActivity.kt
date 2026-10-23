package cl.duoc.wim100

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.duoc.wim100.navigation.WimNavHost
import cl.duoc.wim100.ui.theme.WIM100Theme

/** Única actividad: todo lo demás son pantallas Compose dentro del NavHost. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WIM100Theme {
                WimNavHost()
            }
        }
    }
}
