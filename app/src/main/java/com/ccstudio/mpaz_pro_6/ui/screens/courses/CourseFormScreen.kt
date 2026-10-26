package com.ccstudio.mpaz_pro_6.ui.screens.courses

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.course.CourseForm

/**
 * Formulario de curso.
 *
 * En modo creación pide `nombre` (hasta 50 caracteres) y `anio` (entre 2000 y
 * 2100). En modo edición (`PUT /api/cursos/{id}`) envía solo los campos
 * modificados. El par nombre-año duplicado devuelve `409 Conflict`.
 */
@Composable
fun CourseFormScreen(
    nombre: String,
    anio: String,
    isEditMode: Boolean,
    isLoading: Boolean,
    isSaving: Boolean,
    error: String?,
    onNombreChange: (String) -> Unit,
    onAnioChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            LoadingState(message = "Cargando el curso...")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                CourseForm(
                    nombre = nombre,
                    anio = anio,
                    onNombreChange = onNombreChange,
                    onAnioChange = onAnioChange,
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
