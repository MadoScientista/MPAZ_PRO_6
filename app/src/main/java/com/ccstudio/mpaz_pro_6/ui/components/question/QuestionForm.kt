package com.ccstudio.mpaz_pro_6.ui.components.question

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.Difficulty
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButtonVariant
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTextField
import com.ccstudio.mpaz_pro_6.ui.components.common.FilterChipRow

/**
 * Formulario de pregunta.
 *
 * Reúne el enunciado, el criterio de respuesta, la dificultad y el orden. El
 * estado de los campos llega por parámetro y cada uno expone su callback; la
 * pantalla que lo usa arma el `CreateQuestionRequest` o el `UpdateQuestionRequest`.
 *
 * La actividad, la unidad y el objetivo de aprendizaje no forman parte del
 * formulario: se eligen en la pantalla en modo creación y no se pueden cambiar en
 * edición (`PUT` parcial). El criterio de respuesta y el orden son opcionales.
 */
@Composable
fun QuestionForm(
    enunciado: String,
    criterioRespuesta: String,
    dificultad: Difficulty,
    orden: String,
    onEnunciadoChange: (String) -> Unit,
    onCriterioRespuestaChange: (String) -> Unit,
    onDificultadChange: (Difficulty) -> Unit,
    onOrdenChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isReadOnly: Boolean = false,
    isSaving: Boolean = false,
    error: String? = null
) {
    val ordenValue = orden.trim()
    val isOrdenValid = ordenValue.isEmpty() ||
        (ordenValue.toIntOrNull()?.let { it > 0 } == true)
    val canSubmit = enunciado.isNotBlank() && isOrdenValid

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        AppTextField(
            value = enunciado,
            onValueChange = onEnunciadoChange,
            label = "Enunciado",
            placeholder = "Ej. ¿Qué fracción representa la figura?",
            supportingText = "Obligatorio",
            singleLine = false,
            enabled = !isReadOnly && !isSaving
        )

        AppTextField(
            value = criterioRespuesta,
            onValueChange = onCriterioRespuestaChange,
            label = "Criterio de respuesta",
            placeholder = "Ej. Reconocer el numerador y el denominador",
            supportingText = "Opcional",
            singleLine = false,
            enabled = !isReadOnly && !isSaving
        )

        // Dificultad: selección exclusiva entre BAJA, MEDIA y ALTA.
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Dificultad",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FilterChipRow(
                options = Difficulty.entries.map { it.wireValue },
                selected = dificultad.wireValue,
                onSelect = { wireValue ->
                    val newDifficulty = Difficulty.fromWire(wireValue)
                    if (newDifficulty != null && !isReadOnly && !isSaving) {
                        onDificultadChange(newDifficulty)
                    }
                }
            )
        }

        AppTextField(
            value = orden,
            onValueChange = onOrdenChange,
            label = "Orden",
            placeholder = "Ej. 1",
            supportingText = "Opcional; debe ser mayor que cero",
            enabled = !isReadOnly && !isSaving
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppButton(
                text = if (isEditMode) "Guardar cambios" else "Crear pregunta",
                onClick = onSubmit,
                modifier = Modifier.weight(1f),
                enabled = canSubmit && !isReadOnly && !isSaving,
                variant = AppButtonVariant.PRIMARY
            )

            AppButton(
                text = "Cancelar",
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                enabled = !isSaving,
                variant = AppButtonVariant.OUTLINED
            )
        }
    }
}
