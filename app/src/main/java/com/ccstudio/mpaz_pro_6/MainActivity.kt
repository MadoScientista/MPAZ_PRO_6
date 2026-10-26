package com.ccstudio.mpaz_pro_6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.ccstudio.mpaz_pro_6.data.preferences.ThemeMode
import com.ccstudio.mpaz_pro_6.ui.navigation.AppNavGraph
import com.ccstudio.mpaz_pro_6.ui.theme.MPAZ_PRO_6Theme
import kotlinx.coroutines.launch

/**
 * Punto de entrada de la aplicación.
 *
 * El enrutado vive en `ui/navigation/AppNavGraph` y las dependencias en
 * `data/AppContainer`, creado una sola vez por `MpazApplication`.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // El contenedor vive en la Application: sobrevive a la actividad.
            val container = (application as MpazApplication).container

            // `SYSTEM` es el valor inicial: mientras DataStore responde, la app
            // sigue el modo del teléfono. Después manda la elección guardada.
            val themeMode by container.themePreferenceStore.mode
                .collectAsState(initial = ThemeMode.SYSTEM)
            val isDarkTheme = themeMode.resolveDarkTheme(isSystemInDarkTheme())
            val scope = rememberCoroutineScope()

            MPAZ_PRO_6Theme(darkTheme = isDarkTheme) {
                // La base usa el fondo del tema: el `Scaffold` pinta el mismo rol,
                // así el `LoadingScreen` inicial y los huecos no quedan en blanco.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph(
                        container = container,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = {
                            scope.launch {
                                container.themePreferenceStore.setMode(
                                    themeMode.toggled(isDarkTheme)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
