package com.ccstudio.mpaz_pro_6.ui.screens.subjects

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.ThemeToggleButton
import com.ccstudio.mpaz_pro_6.ui.components.subject.SubjectForm

/**
 * Formulario de asignatura.
 *
 * En modo creación (`POST /api/asignaturas`) pide solo el `nombre`, obligatorio y
 * de hasta 100 caracteres. En modo edición (`PUT /api/asignaturas/{id}`) envía el
 * nombre actual, porque el contrato lo exige aunque la actualización sea parcial.
 */
@Composable
fun SubjectFormScreen(
    nombre: String,
    isEditMode: Boolean,
    isLoading: Boolean,
    isSaving: Boolean,
    error: String?,
    onNombreChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(
            title = if (isEditMode) "Editar asignatura" else "Nueva asignatura",
            onNavigateBack = onNavigateBack,
            actions = {
                ThemeToggleButton(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }
        )

        if (isLoading) {
            LoadingState(message = "Cargando la asignatura...")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                SubjectForm(
                    nombre = nombre,
                    onNombreChange = onNombreChange,
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
