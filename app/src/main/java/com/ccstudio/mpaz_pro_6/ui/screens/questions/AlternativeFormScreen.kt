package com.ccstudio.mpaz_pro_6.ui.screens.questions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.ui.components.question.AlternativeForm

/** Formulario de creación de una alternativa para una pregunta. */
@Composable
fun AlternativeFormScreen(
    texto: String,
    esCorrecta: Boolean,
    isSaving: Boolean,
    error: String?,
    onTextoChange: (String) -> Unit,
    onEsCorrectaChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AlternativeForm(
            texto = texto,
            esCorrecta = esCorrecta,
            onTextoChange = onTextoChange,
            onEsCorrectaChange = onEsCorrectaChange,
            onSubmit = onSubmit,
            onCancel = onCancel,
            isSaving = isSaving,
            error = error
        )
    }
}
