package com.ccstudio.mpaz_pro_6.ui.screens.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.theme.MPAZ_PRO_6Theme
import org.junit.Rule
import org.junit.Test

/**
 * Pruebas de regresión del panel del estudiante.
 *
 * La unidad y la actividad comparten id a propósito: el `LazyColumn` debe usar
 * claves distintas para ambos tipos de elemento.
 */
class HomeStudentScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private val unit = LearningUnit(
        id = 1,
        asignaturaId = 1,
        nivelEducativo = "Sexto basico",
        titulo = "Unidad 1",
        orden = 1,
        estado = ContentStatus.PUBLISHED,
        autorizadaPorId = 1,
        fechaAutorizacion = null
    )

    private val activity = Activity(
        id = 1,
        unidadId = 1,
        titulo = "Actividad con el mismo id que la unidad",
        tipo = ActivityType.ACTIVITY,
        orden = 1
    )

    @Test
    fun scrolleaSinColisionDeClavesEntreUnidadYActividad() {
        rule.setContent {
            MPAZ_PRO_6Theme {
                HomeStudentScreen(
                    unidades = listOf(unit),
                    asignaturas = listOf(Subject(id = 1, nombre = "Lengua")),
                    actividadesPendientes = listOf(activity),
                    totalInformes = 1,
                    sinCurso = false,
                    onViewUnit = {},
                    onVerUnidades = {},
                    onResolveActivity = {},
                    onCargar = {}
                )
            }
        }

        rule.onNodeWithTag("home-student-list")
            .performScrollToNode(hasText(activity.titulo))

        rule.onNodeWithText(activity.titulo).assertIsDisplayed()
    }
}
