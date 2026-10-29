package com.ccstudio.mpaz_pro_6.ui.screens.attempts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Alternative
import com.ccstudio.mpaz_pro_6.data.model.Attempt
import com.ccstudio.mpaz_pro_6.data.model.Question
import com.ccstudio.mpaz_pro_6.ui.components.activity.ActivityTypeChip
import com.ccstudio.mpaz_pro_6.ui.components.attempt.AttemptProgress
import com.ccstudio.mpaz_pro_6.ui.components.attempt.AttemptQuestionCard
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader

/**
 * Pantalla de resolución de una actividad.
 *
 * Si no hay un intento en progreso, ofrece comenzar uno. Con un intento activo,
 * muestra el progreso y las preguntas con sus alternativas; cada selección se
 * registra en el backend. Al completar, navega al resultado. Un intento ya
 * cerrado se presenta en modo de solo lectura con la corrección congelada.
 *
 * En **vista previa** (UTP y docente) la actividad se abre directamente, sin
 * ofrecer «Comenzar intento»: las alternativas correctas aparecen seleccionadas,
 * no hay barra de progreso y el botón «Finalizar intento» queda deshabilitado
 * porque la vista previa no registra intentos ni respuestas.
 */
@Composable
fun ActivityAttemptScreen(
    activity: Activity?,
    questions: List<Question>,
    alternativesByQuestion: Map<Int, List<Alternative>>,
    attempt: Attempt?,
    selectedAlternatives: Map<Int, Int>,
    registeredQuestionIds: Set<Int>,
    answeredCount: Int,
    allAnswered: Boolean,
    isLoading: Boolean,
    isSaving: Boolean,
    error: ApiError?,
    mensaje: String?,
    onStart: () -> Unit,
    onSelectAlternative: (Int, Int) -> Unit,
    onComplete: () -> Unit,
    onViewResult: (Int) -> Unit,
    onRetry: () -> Unit,
    onDismissMensaje: () -> Unit,
    modifier: Modifier = Modifier,
    previewMode: Boolean = false
) {
    val isCompleted = attempt?.fechaFin != null

    Column(modifier = modifier.fillMaxSize()) {
        when {
            isLoading && activity == null -> LoadingState(message = "Cargando la actividad...")

            error != null -> ErrorState(error = error, onRetry = onRetry)

            activity == null -> EmptyState(
                message = "No se encontró la actividad.",
                modifier = Modifier.padding(16.dp)
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ActivityHeader(
                        activity = activity,
                        attempt = attempt
                    )
                }

                if (mensaje != null) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = mensaje,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            TextButton(onClick = onDismissMensaje) {
                                Text("Cerrar")
                            }
                        }
                    }
                }

                if (previewMode) {
                    if (questions.isEmpty()) {
                        item {
                            EmptyState(
                                message = "La actividad no tiene preguntas.",
                                description = "Todavía no se puede previsualizar."
                            )
                        }
                    } else {
                        item {
                            Text(
                                text = "Vista previa: las alternativas correctas aparecen " +
                                        "seleccionadas. No se registra ningún intento.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        item {
                            SectionHeader(
                                title = "Preguntas",
                                count = questions.size
                            )
                        }

                        itemsIndexed(
                            items = questions,
                            key = { _, question -> question.id }
                        ) { index, question ->
                            AttemptQuestionCard(
                                question = question,
                                alternatives = alternativesByQuestion[question.id].orEmpty(),
                                selectedAlternativeId = selectedAlternatives[question.id],
                                onSelectAlternative = {},
                                questionNumber = index + 1,
                                isReadOnly = true,
                                enabled = false
                            )
                        }

                        item {
                            AppButton(
                                text = "Finalizar intento",
                                onClick = {},
                                enabled = false
                            )

                            Text(
                                text = "La vista previa no registra intentos ni respuestas.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else if (attempt == null) {
                    if (questions.isEmpty()) {
                        item {
                            EmptyState(
                                message = "La actividad no tiene preguntas.",
                                description = "Todavía no se puede resolver."
                            )
                        }
                    } else {
                        item {
                            Text(
                                text = "Vas a iniciar un nuevo intento. " +
                                        "Responde todas las preguntas y finaliza para ver tu resultado.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        item {
                            AppButton(
                                text = "Comenzar intento",
                                onClick = onStart,
                                enabled = !isSaving
                            )
                        }
                    }
                } else {
                    item {
                        AttemptProgress(
                            attempt = attempt,
                            answeredCount = answeredCount,
                            totalQuestions = questions.size
                        )
                    }

                    item {
                        SectionHeader(
                            title = "Preguntas",
                            count = questions.size
                        )
                    }

                    if (questions.isEmpty()) {
                        item {
                            EmptyState(
                                message = "La actividad no tiene preguntas.",
                                description = "Todavía no se puede resolver."
                            )
                        }
                    } else {
                        itemsIndexed(
                            items = questions,
                            key = { _, question -> question.id }
                        ) { index, question ->
                            AttemptQuestionCard(
                                question = question,
                                alternatives = alternativesByQuestion[question.id].orEmpty(),
                                selectedAlternativeId = selectedAlternatives[question.id],
                                onSelectAlternative = { alternativeId ->
                                    onSelectAlternative(question.id, alternativeId)
                                },
                                questionNumber = index + 1,
                                isReadOnly = isCompleted,
                                isLocked = question.id in registeredQuestionIds,
                                showCorrectness = isCompleted,
                                enabled = !isSaving && question.id !in registeredQuestionIds
                            )
                        }

                        item {
                            if (isCompleted) {
                                AppButton(
                                    text = "Ver resultado",
                                    onClick = { onViewResult(attempt.id) }
                                )
                            } else {
                                AppButton(
                                    text = "Finalizar intento",
                                    onClick = onComplete,
                                    enabled = allAnswered && !isSaving
                                )

                                if (!allAnswered) {
                                    Text(
                                        text = "Responde todas las preguntas para finalizar.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Encabezado con el título, el tipo y el número de intento. */
@Composable
private fun ActivityHeader(
    activity: Activity,
    attempt: Attempt?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = activity.titulo,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActivityTypeChip(activityType = activity.tipo)


            if (attempt != null) {
                Text(
                    text = "Intento ${attempt.numero}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
