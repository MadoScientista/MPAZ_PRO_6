package com.ccstudio.mpaz_pro_6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ccstudio.mpaz_pro_6.ui.navigation.AppNavGraph
import com.ccstudio.mpaz_pro_6.ui.theme.MPAZ_PRO_6Theme

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
            MPAZ_PRO_6Theme {

                // El contenedor vive en la Application: sobrevive a la actividad.
                val container = (application as MpazApplication).container

                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph(container = container)
                }
            }
        }
    }
}
