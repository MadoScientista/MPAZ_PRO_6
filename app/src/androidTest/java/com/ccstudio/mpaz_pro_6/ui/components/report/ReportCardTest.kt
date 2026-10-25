package com.ccstudio.mpaz_pro_6.ui.components.report

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import com.ccstudio.mpaz_pro_6.data.enums.ReportStatus
import com.ccstudio.mpaz_pro_6.data.model.PedagogicalReport
import com.ccstudio.mpaz_pro_6.ui.theme.MPAZ_PRO_6Theme
import org.junit.Rule
import org.junit.Test
import java.time.Instant

/**
 * Pruebas de [ReportCard].
 *
 * Cubren la acción «Ver informe» del docente y el modo de presentación del
 * estudiante, que usa la unidad como título.
 */
class ReportCardTest {

    @get:Rule
    val rule = createComposeRule()

    private val report = PedagogicalReport(
        id = 1,
        estudianteId = 3,
        unidadId = 1,
        docenteId = 2,
        fechaGeneracion = Instant.parse("2026-05-01T10:00:00Z"),
        fechaValidacion = null,
        estado = ReportStatus.PENDING_VALIDATION
    )

    @Test
    fun muestraVerInformeConLasAccionesHabilitadas() {
        rule.setContent {
            MPAZ_PRO_6Theme(dynamicColor = false) {
                ReportCard(
                    report = report,
                    studentName = "Camila Rivas",
                    unitName = "Lectura y comprehension de textos",
                    onViewDetail = {}
                )
            }
        }

        rule.onNodeWithText("Ver informe").assertIsDisplayed()
        rule.onNodeWithText("Camila Rivas").assertIsDisplayed()
    }

    @Test
    fun ocultaVerInformeCuandoLasAccionesEstanDeshabilitadas() {
        rule.setContent {
            MPAZ_PRO_6Theme(dynamicColor = false) {
                ReportCard(
                    report = report,
                    studentName = "Camila Rivas",
                    unitName = "Lectura y comprehension de textos",
                    onViewDetail = {},
                    showActions = false
                )
            }
        }

        rule.onAllNodesWithText("Ver informe").assertCountEquals(0)
    }

    @Test
    fun estudianteVeLaUnidadComoTituloYNoSuNombre() {
        rule.setContent {
            MPAZ_PRO_6Theme(dynamicColor = false) {
                ReportCard(
                    report = report,
                    studentName = "Camila Rivas",
                    unitName = "Lectura y comprehension de textos",
                    onViewDetail = {},
                    showStudentName = false
                )
            }
        }

        rule.onNodeWithText("Lectura y comprehension de textos").assertIsDisplayed()
        rule.onAllNodesWithText("Camila Rivas").assertCountEquals(0)
    }
}
