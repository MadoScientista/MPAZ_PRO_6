package com.ccstudio.mpaz_pro_6.ui.screens.users

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.ThemeToggleButton
import com.ccstudio.mpaz_pro_6.ui.components.user.UserForm

/**
 * Formulario de usuario.
 *
 * En modo creación pide `nombre`, `username`, `password` y `rol`; `cursoId` es
 * opcional y solo se admite para estudiantes. En modo edición
 * (`PUT /api/usuarios/{id}`) envía solo los campos modificados y una contraseña
 * vacía no se envía.
 */
@Composable
fun UserFormScreen(
    nombre: String,
    username: String,
    password: String,
    rol: UserRole,
    cursoId: Int?,
    cursos: List<Course>,
    isEditMode: Boolean,
    isLoading: Boolean,
    isSaving: Boolean,
    error: String?,
    onNombreChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRolChange: (UserRole) -> Unit,
    onCursoIdChange: (Int?) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(
            title = if (isEditMode) "Editar usuario" else "Nuevo usuario",
            onNavigateBack = onNavigateBack,
            actions = {
                ThemeToggleButton(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }
        )

        if (isLoading) {
            LoadingState(message = "Cargando el usuario...")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                UserForm(
                    nombre = nombre,
                    username = username,
                    password = password,
                    rol = rol,
                    cursoId = cursoId,
                    courses = cursos,
                    onNombreChange = onNombreChange,
                    onUsernameChange = onUsernameChange,
                    onPasswordChange = onPasswordChange,
                    onRolChange = onRolChange,
                    onCursoIdChange = onCursoIdChange,
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
