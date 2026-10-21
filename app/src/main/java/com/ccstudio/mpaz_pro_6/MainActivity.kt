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
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.screens.auth.LoginScreen
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitListScreen
import com.ccstudio.mpaz_pro_6.ui.theme.MPAZ_PRO_6Theme
import java.time.Instant

/**
 * Punto de entrada de la aplicación.
 *
 * Mientras no exista `navigation-compose`, el flujo se resuelve con un estado local
 * que alterna entre el login y el listado de unidades. Cuando se implemente
 * `AppNavGraph`, este archivo debe delegar el enrutado por rol (`UserRole`) y
 * dejar de mantener la lógica de navegación aquí.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MPAZ_PRO_6Theme {

                // Controla qué pantalla se muestra.
                var showUnits by rememberSaveable {
                    mutableStateOf(false)
                }

                // Datos ficticios para probar la interfaz.
                val subjects = remember {
                    mutableStateListOf(
                        Subject(id = 1, nombre = "Matemática"),
                        Subject(id = 2, nombre = "Ciencias Naturales"),
                        Subject(id = 3, nombre = "Lenguaje")
                    )
                }

                val units = remember {
                    mutableStateListOf(
                        LearningUnit(
                            id = 1,
                            asignaturaId = 1,
                            nivelEducativo = "Sexto Basico",
                            titulo = "Números enteros y operaciones",
                            orden = 1,
                            estado = ContentStatus.DRAFT,
                            autorizadaPorId = null,
                            fechaAutorizacion = null
                        ),
                        LearningUnit(
                            id = 2,
                            asignaturaId = 2,
                            nivelEducativo = "Sexto Basico",
                            titulo = "Ecosistemas y biodiversidad",
                            orden = 2,
                            estado = ContentStatus.IN_REVIEW,
                            autorizadaPorId = null,
                            fechaAutorizacion = null
                        ),
                        LearningUnit(
                            id = 3,
                            asignaturaId = 3,
                            nivelEducativo = "Sexto Basico",
                            titulo = "Comprensión de textos narrativos",
                            orden = 1,
                            estado = ContentStatus.AUTHORIZED,
                            autorizadaPorId = 1,
                            fechaAutorizacion = Instant.parse("2026-04-06T08:00:00Z")
                        )
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    if (showUnits) {

                        // Pantalla de revisión de unidades.
                        UnitListScreen(
                            pendingUnits = units,
                            subjects = subjects,

                            onViewDetail = { unit ->
                                // Aquí conectaremos la navegación
                                // a la pantalla de detalle.
                            },

                            onAuthorize = { unit ->
                                // Simulación de autorización.
                                units.removeAll {
                                    it.id == unit.id
                                }
                            },

                            modifier = Modifier.padding(innerPadding)
                        )

                    } else {

                        // Pantalla de inicio de sesión.
                        LoginScreen(
                            onIniciarSesion = { _: String, _: String ->
                                showUnits = true
                            },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
