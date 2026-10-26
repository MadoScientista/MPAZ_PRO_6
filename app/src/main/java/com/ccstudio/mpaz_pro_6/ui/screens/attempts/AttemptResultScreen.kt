package com.ccstudio.mpaz_pro_6.ui.screens.attempts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Alternative
import com.ccstudio.mpaz_pro_6.data.model.Attempt
import com.ccstudio.mpaz_pro_6.data.model.Question
import com.ccstudio.mpaz_pro_6.data.model.StudentAnswer
import com.ccstudio.mpaz_pro_6.ui.components.activity.ActivityTypeChip
import com.ccstudio.mpaz_pro_6.ui.components.attempt.AttemptProgress
import com.ccstudio.mpaz_pro_6.ui.components.attempt.AttemptQuestionCard
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.common.ThemeToggleButton

/**
 * Resultado de un intento.
 *
 * Muestra el resumen de respuestas y la corrección congelada por el backend. El
 * estado de finalización se deduce de `fechaFin`: nulo significa intento en
 * progreso.
 */
@Composable
fun AttemptResultScreen(
    activity: Activity?,
    attempt: Attempt?,
    questions: List<Question>,
    alternativesByQuestion: Map<Int, List<Alternative>>,
    answers: Map<Int, StudentAnswer>,
    correctas: Int,
    total: Int,
    porcentaje: Int,
    isLoading: Boolean,
    error: ApiError?,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = attempt?.fechaFin != null

    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(
            title = "Resultado del intento",
            onNavigateBack = onNavigateBack,
            actions = {
                ThemeToggleButton(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }
        )

        when {
            isLoading && attempt == null -> LoadingState(message = "Cargando el resultado...")

            error != null -> ErrorState(error = error, onRetry = onRetry)

            attempt == null -> EmptyState(
                message = "No se encontró el intento.",
                modifier = Modifier.padding(16.dp)
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ResumenCard(
                        activity = activity,
                        attempt = attempt,
                        correctas = correctas,
                        total = total,
                        porcentaje = porcentaje
                    )
                }

                item {
                    AttemptProgress(
                        attempt = attempt,
                        answeredCount = answers.size,
                        totalQuestions = total
                    )
                }

                item {
                    SectionHeader(
                        title = "Detalle de respuestas",
                        count = questions.size
                    )
                }

                if (questions.isEmpty()) {
                    item {
                        EmptyState(message = "La actividad no tiene preguntas.")
                    }
                } else {
                    itemsIndexed(
                        items = questions,
                        key = { _, question -> question.id }
                    ) { index, question ->
                        AttemptQuestionCard(
                            question = question,
                            alternatives = alternativesByQuestion[question.id].orEmpty(),
                            selectedAlternativeId = answers[question.id]?.alternativaId,
                            onSelectAlternative = {},
                            questionNumber = index + 1,
                            isReadOnly = isCompleted,
                            showCorrectness = isCompleted,
                            enabled = false
                        )
                    }
                }
            }
        }
    }
}

/** Resumen de la actividad y del puntaje del intento. */
@Composable
private fun ResumenCard(
    activity: Activity?,
    attempt: Attempt,
    correctas: Int,
    total: Int,
    porcentaje: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (activity != null) {
                ActivityTypeChip(activityType = activity.tipo)

                Text(
                    text = activity.titulo,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Intento ${attempt.numero}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Correctas: $correctas de $total ($porcentaje%)",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
