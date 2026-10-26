package com.ccstudio.mpaz_pro_6.ui.components.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButtonVariant
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTextField
import com.ccstudio.mpaz_pro_6.ui.components.common.FilterChipRow

/**
 * Formulario de usuario.
 *
 * Reúne el nombre, el nombre de usuario, la contraseña, el rol y el curso. El
 * estado de los campos llega por parámetro y cada uno expone su callback; la
 * pantalla que lo usa arma el `CreateUserRequest` o el `UpdateUserRequest`.
 *
 * El selector de curso solo se muestra cuando el rol es `ESTUDIANTE`. En modo
 * edición la contraseña es opcional y los campos vacíos no se envían (`PUT`
 * parcial).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserForm(
    nombre: String,
    username: String,
    password: String,
    rol: UserRole,
    cursoId: Int?,
    courses: List<Course>,
    onNombreChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRolChange: (UserRole) -> Unit,
    onCursoIdChange: (Int?) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isReadOnly: Boolean = false,
    isSaving: Boolean = false,
    error: String? = null
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var courseMenuExpanded by rememberSaveable { mutableStateOf(false) }

    val isUsernameValid = username.trim().matches(USERNAME_PATTERN)
    val canSubmit = nombre.isNotBlank() &&
        isUsernameValid &&
        (isEditMode || password.isNotBlank())

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        AppTextField(
            value = nombre,
            onValueChange = onNombreChange,
            label = "Nombre",
            placeholder = "Ej. María Gómez",
            enabled = !isReadOnly && !isSaving
        )

        AppTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = "Usuario",
            placeholder = "Ej. mgomez",
            supportingText = "Entre 3 y 100 caracteres: letras, números, punto, guion o guion bajo",
            enabled = !isReadOnly && !isSaving
        )

        AppTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Contraseña",
            isPassword = true,
            passwordVisible = passwordVisible,
            placeholder = if (isEditMode) "Dejar vacío si no se cambia" else "Contraseña del usuario",
            supportingText = if (isEditMode) {
                "Dejar vacío para conservar la contraseña actual"
            } else {
                "Obligatoria para crear el usuario"
            },
            enabled = !isReadOnly && !isSaving,
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        },
                        contentDescription = if (passwordVisible) {
                            "Ocultar contraseña"
                        } else {
                            "Mostrar contraseña"
                        }
                    )
                }
            }
        )

        FilterChipRow(
            options = UserRole.entries.map { it.wireValue },
            selected = rol.wireValue,
            onSelect = { wireValue ->
                val newRole = UserRole.fromWire(wireValue)
                if (newRole != null && !isReadOnly && !isSaving) {
                    onRolChange(newRole)
                    if (newRole != UserRole.ESTUDIANTE) {
                        onCursoIdChange(null)
                    }
                }
            }
        )

        if (rol == UserRole.ESTUDIANTE) {
            val selectedCourseLabel = courses
                .firstOrNull { it.id == cursoId }
                ?.let { "${it.nombre} (${it.anio})" }
                .orEmpty()

            ExposedDropdownMenuBox(
                expanded = courseMenuExpanded,
                onExpandedChange = {
                    if (!isReadOnly && !isSaving) {
                        courseMenuExpanded = !courseMenuExpanded
                    }
                }
            ) {
                OutlinedTextField(
                    value = selectedCourseLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(text = "Curso") },
                    placeholder = { Text(text = "Seleccionar curso") },
                    supportingText = { Text(text = "Solo para estudiantes") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = courseMenuExpanded
                        )
                    },
                    enabled = !isReadOnly && !isSaving,
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = courseMenuExpanded,
                    onDismissRequest = { courseMenuExpanded = false },
                    shadowElevation = 0.dp
                ) {
                    DropdownMenuItem(
                        text = { Text(text = "Sin curso") },
                        onClick = {
                            onCursoIdChange(null)
                            courseMenuExpanded = false
                        }
                    )

                    courses.forEach { course ->
                        DropdownMenuItem(
                            text = { Text(text = "${course.nombre} (${course.anio})") },
                            onClick = {
                                onCursoIdChange(course.id)
                                courseMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

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
                text = if (isEditMode) "Guardar cambios" else "Crear usuario",
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

/** Regla del contrato para el `username`: 3 a 100 caracteres permitidos. */
private val USERNAME_PATTERN = Regex("^[A-Za-z0-9._-]{3,100}$")
