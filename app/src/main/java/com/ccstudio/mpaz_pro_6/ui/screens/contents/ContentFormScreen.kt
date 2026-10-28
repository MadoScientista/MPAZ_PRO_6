package com.ccstudio.mpaz_pro_6.ui.screens.contents

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
import com.ccstudio.mpaz_pro_6.data.enums.ContentOrigin
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.FilterChipRow
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.content.ContentForm

/**
 * Formulario de contenido.
 *
 * En modo creación pide `oaId`, `titulo`, `explicacion` y `origen`; `ejemplos`,
 * `instrucciones` y `orden` son opcionales. En modo edición envía solo los campos
 * modificados.
 *
 * La unidad no se elige ni se envía: el backend la copia del objetivo de
 * aprendizaje y la devuelve como `unidadId`. El OA es el único selector y se
 * muestra como chips con el código de cada objetivo de la unidad.
 */
@Composable
fun ContentFormScreen(
    objetivos: List<LearningObjective>,
    selectedObjectiveId: Int?,
    titulo: String,
    explicacion: String,
    ejemplos: String,
    instrucciones: String,
    orden: String,
    origen: ContentOrigin,
    isLoading: Boolean,
    isSaving: Boolean,
    loadError: ApiError?,
    error: String?,
    onObjectiveChange: (Int) -> Unit,
    onTituloChange: (String) -> Unit,
    onExplicacionChange: (String) -> Unit,
    onEjemplosChange: (String) -> Unit,
    onInstruccionesChange: (String) -> Unit,
    onOrdenChange: (String) -> Unit,
    onOrigenChange: (ContentOrigin) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false
) {
    val selectedLabel = objetivos
        .firstOrNull { it.id == selectedObjectiveId }
        ?.let(::objectiveLabel)
        .orEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when {
            isLoading && objetivos.isEmpty() -> LoadingState(
                message = "Cargando objetivos de aprendizaje..."
            )

            loadError != null -> ErrorState(error = loadError, onRetry = onRetry)

            objetivos.isEmpty() -> EmptyState(
                message = "Sin objetivos de aprendizaje",
                description = "La unidad necesita al menos un objetivo antes de crear contenidos."
            )

            else -> {
                SectionHeader(title = "Objetivo de aprendizaje")

                Text(
                    text = "El contenido queda asociado al objetivo que elijas y no se puede cambiar después.",
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

                ContentForm(
                    titulo = titulo,
                    explicacion = explicacion,
                    ejemplos = ejemplos,
                    instrucciones = instrucciones,
                    orden = orden,
                    origen = origen,
                    onTituloChange = onTituloChange,
                    onExplicacionChange = onExplicacionChange,
                    onEjemplosChange = onEjemplosChange,
                    onInstruccionesChange = onInstruccionesChange,
                    onOrdenChange = onOrdenChange,
                    onOrigenChange = onOrigenChange,
                    onSubmit = onSubmit,
                    onCancel = onCancel,
                    isEditMode = isEditMode,
                    isSaving = isSaving,
                    error = error
                )
            }
        }
    }
}

/**
 * Etiqueta del objetivo en los chips.
 *
 * Usa el código del objetivo; si no tiene, cae al identificador para que el
 * usuario siempre pueda distinguir una opción de otra.
 */
private fun objectiveLabel(objective: LearningObjective): String =
    objective.codigo?.takeIf { it.isNotBlank() } ?: "OA ${objective.id}"
