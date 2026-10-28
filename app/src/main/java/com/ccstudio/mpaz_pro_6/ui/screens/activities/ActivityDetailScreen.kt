package com.ccstudio.mpaz_pro_6.ui.screens.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Alternative
import com.ccstudio.mpaz_pro_6.data.model.Question
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.question.AlternativeRow
import com.ccstudio.mpaz_pro_6.ui.components.question.QuestionCard

/**
 * Detalle de una actividad para el docente.
 *
 * Muestra las preguntas y sus alternativas. Si [canManage] es `true`, permite
 * agregar preguntas y alternativas; la UTP puede consultar el mismo detalle en
 * modo lectura.
 */
@Composable
fun ActivityDetailScreen(
    activity: Activity?,
    questions: List<Question>,
    alternativesByQuestion: Map<Int, List<Alternative>>,
    canManage: Boolean,
    isLoading: Boolean,
    error: ApiError?,
    onAddQuestion: () -> Unit,
    onAddAlternative: (Question) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        isLoading && activity == null -> LoadingState(
            message = "Cargando la actividad...",
            modifier = Modifier.padding(16.dp)
        )

        error != null && activity == null -> ErrorState(
            error = error,
            onRetry = onRetry,
            modifier = Modifier.padding(16.dp)
        )

        activity == null -> EmptyState(
            message = "No se encontró la actividad.",
            modifier = Modifier.padding(16.dp)
        )

        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = activity.titulo,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = activity.tipo.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (canManage) {
                item {
                    AppButton(
                        text = "Agregar pregunta",
                        onClick = onAddQuestion
                    )
                }
            }

            item {
                SectionHeader(title = "Preguntas", count = questions.size)
            }

            if (questions.isEmpty()) {
                item {
                    EmptyState(
                        message = "Esta actividad todavía no tiene preguntas.",
                        description = if (canManage) {
                            "Agrega una pregunta para que el estudiante pueda resolverla."
                        } else {
                            null
                        }
                    )
                }
            } else {
                items(questions, key = { it.id }) { question ->
                    val alternatives = alternativesByQuestion[question.id].orEmpty()

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        QuestionCard(
                            question = question,
                            alternativesCount = alternatives.size,
                            onEdit = null,
                            onDelete = null
                        )

                        alternatives.forEach { alternative ->
                            AlternativeRow(
                                alternative = alternative,
                                onEdit = {},
                                onDelete = {},
                                isReadOnly = true
                            )
                        }

                        if (canManage) {
                            AppButton(
                                text = "Agregar alternativa",
                                onClick = { onAddAlternative(question) }
                            )
                        }
                    }
                }
            }

            if (error != null) {
                item {
                    ErrorState(error = error, onRetry = onRetry)
                }
            }
        }
    }
}
