package com.ccstudio.mpaz_pro_6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.ccstudio.mpaz_pro_6.data.AppContainer
import com.ccstudio.mpaz_pro_6.ui.navigation.AppNavGraph
import com.ccstudio.mpaz_pro_6.ui.theme.MPAZ_PRO_6Theme

/**
 * Punto de entrada de la aplicación.
 *
 * El enrutado vive en `ui/navigation/AppNavGraph` y las dependencias en
 * `data/AppContainer`. Este archivo solo crea el contenedor y monta el grafo: cuando
 * exista el backend, se cambia la implementación de los repositorios en
 * `AppContainer` y ni la UI ni la navegación se tocan.
 *
 * La actividad declara `android:configChanges` en el manifiesto, así que no se
 * recrea al girar el dispositivo: el contenedor, la sesión y los datos en memoria
 * sobreviven al cambio de orientación.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MPAZ_PRO_6Theme {

                // Contenedor de dependencias: sobrevive a los cambios de configuración
                // porque la actividad no se recrea al girar el dispositivo.
                val container = remember { AppContainer() }

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        // La app dibuja de borde a borde: sin este margen el contenido
                        // inferior (botones y última tarjeta) quedaría bajo la barra de
                        // navegación del sistema.
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    AppNavGraph(container = container)
                }
            }
        }
    }
}
