package com.ccstudio.mpaz_pro_6.ui.components.attempt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.ccstudio.mpaz_pro_6.data.enums.Difficulty
import com.ccstudio.mpaz_pro_6.data.model.Alternative
import com.ccstudio.mpaz_pro_6.data.model.Question
import com.ccstudio.mpaz_pro_6.ui.theme.MPAZ_PRO_6Theme
import org.junit.Rule
import org.junit.Test

/**
 * Pruebas de interacción de [AttemptQuestionCard].
 *
 * Cubren el cambio de alternativa seleccionada: el estudiante debe poder
 * elegir otra opción antes de finalizar el intento.
 */
class AttemptQuestionCardTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun permiteCambiarLaAlternativaSeleccionada() {
        rule.setContent {
            MPAZ_PRO_6Theme(dynamicColor = false) {
                var selected by remember { mutableStateOf<Int?>(null) }

                AttemptQuestionCard(
                    question = Question(
                        id = 10,
                        actividadId = 1,
                        unidadId = 1,
                        oaId = 1,
                        enunciado = "Cual es la idea principal del texto",
                        criterioRespuesta = null,
                        dificultad = Difficulty.LOW,
                        orden = 1
                    ),
                    alternatives = listOf(
                        Alternative(
                            id = 1,
                            preguntaId = 10,
                            texto = "Alternativa A",
                            esCorrecta = false
                        ),
                        Alternative(
                            id = 2,
                            preguntaId = 10,
                            texto = "Alternativa B",
                            esCorrecta = true
                        )
                    ),
                    selectedAlternativeId = selected,
                    onSelectAlternative = { selected = it }
                )
            }
        }

        rule.onNodeWithTag("alternative-1").performClick()
        rule.onNodeWithTag("alternative-1").assertIsSelected()

        rule.onNodeWithTag("alternative-2").performClick()
        rule.onNodeWithTag("alternative-2").assertIsSelected()
        rule.onNodeWithTag("alternative-1").assertIsNotSelected()
    }
}
