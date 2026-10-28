package com.ccstudio.mpaz_pro_6.ui.screens.questions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.Difficulty
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.FilterChipRow
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.question.QuestionForm

/**
 * Formulario de creación de una pregunta.
 *
 * El OA se elige entre los objetivos de la unidad; la actividad y la unidad no
 * se modifican. La pantalla se abre desde el detalle de la actividad.
 */
@Composable
fun QuestionFormScreen(
    objetivos: List<LearningObjective>,
    selectedObjectiveId: Int?,
    enunciado: String,
    criterioRespuesta: String,
    dificultad: Difficulty,
    orden: String,
    isLoading: Boolean,
    isSaving: Boolean,
    loadError: ApiError?,
    error: String?,
    onObjectiveChange: (Int) -> Unit,
    onEnunciadoChange: (String) -> Unit,
    onCriterioRespuestaChange: (String) -> Unit,
    onDificultadChange: (Difficulty) -> Unit,
    onOrdenChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedLabel = objetivos
        .firstOrNull { it.id == selectedObjectiveId }
        ?.let(::objectiveLabel)
        .orEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when {
            isLoading && objetivos.isEmpty() -> LoadingState(
                message = "Cargando objetivos de aprendizaje..."
            )

            loadError != null -> ErrorState(error = loadError, onRetry = onRetry)

            objetivos.isEmpty() -> EmptyState(
                message = "Sin objetivos de aprendizaje",
                description = "La unidad necesita al menos un objetivo antes de crear preguntas."
            )

            else -> {
                SectionHeader(title = "Objetivo de aprendizaje")

                Text(
                    text = "La pregunta quedará asociada al objetivo que elijas.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FilterChipRow(
                    options = objetivos.map(::objectiveLabel),
                    selected = selectedLabel,
                    onSelect = { label ->
                        objetivos.firstOrNull { objectiveLabel(it) == label }
                            ?.let { onObjectiveChange(it.id) }
                    }
                )

                QuestionForm(
                    enunciado = enunciado,
                    criterioRespuesta = criterioRespuesta,
                    dificultad = dificultad,
                    orden = orden,
                    onEnunciadoChange = onEnunciadoChange,
                    onCriterioRespuestaChange = onCriterioRespuestaChange,
                    onDificultadChange = onDificultadChange,
                    onOrdenChange = onOrdenChange,
                    onSubmit = onSubmit,
                    onCancel = onCancel,
                    isSaving = isSaving,
                    error = error
                )
            }
        }
    }
}

private fun objectiveLabel(objective: LearningObjective): String =
    objective.codigo?.takeIf { it.isNotBlank() } ?: "OA ${objective.id}"
