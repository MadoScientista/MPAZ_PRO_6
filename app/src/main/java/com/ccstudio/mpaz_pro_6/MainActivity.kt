package com.ccstudio.mpaz_pro_6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ccstudio.mpaz_pro_6.model.Usuario
import com.ccstudio.mpaz_pro_6.ui.auth.Login
import com.ccstudio.mpaz_pro_6.ui.theme.MPAZ_PRO_6Theme
import com.ccstudio.mpaz_pro_6.ui.utp.UnitListScreen
import com.ccstudio.mpaz_pro_6.ui.utp.UnidadPendiente

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MPAZ_PRO_6Theme {

                // Controla qué pantalla se muestra.
                var mostrarUnidades by rememberSaveable {
                    mutableStateOf(false)
                }

                // Datos ficticios para probar la interfaz.
                val unidades = remember {
                    mutableStateListOf(
                        UnidadPendiente(
                            id = 1,
                            asignatura = "Matemática",
                            titulo = "Números enteros y operaciones",
                            cantidadActividades = 6
                        ),
                        UnidadPendiente(
                            id = 2,
                            asignatura = "Ciencias Naturales",
                            titulo = "Ecosistemas y biodiversidad",
                            cantidadActividades = 4
                        ),
                        UnidadPendiente(
                            id = 3,
                            asignatura = "Lenguaje",
                            titulo = "Comprensión de textos narrativos",
                            cantidadActividades = 5
                        )
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    if (mostrarUnidades) {

                        // Pantalla de revisión de unidades.
                        UnitListScreen(
                            unidadesPendientes = unidades,

                            onVerDetalle = { unidad ->
                                // Aquí conectaremos la navegación
                                // a la pantalla de detalle.
                            },

                            onAutorizar = { unidad ->
                                // Simulación de autorización.
                                unidades.removeAll {
                                    it.id == unidad.id
                                }
                            },

                            modifier = Modifier.padding(innerPadding)
                        )

                    } else {

                        // Pantalla de inicio de sesión.
                        Login().FormScreen(
                            onNavegarADetalle = { _: Usuario ->
                                mostrarUnidades = true
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}