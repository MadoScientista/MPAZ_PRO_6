package com.ccstudio.mpaz_pro_6.ui.screens.docencias

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.docencia.DocenciaForm

/**
 * Formulario de docencia.
 *
 * Al crear pide docente, curso y asignatura; al editar solo permite cambiar el
 * docente. El curso y la asignatura se muestran bloqueados porque el `PUT` de
 * docencias no los admite.
 */
@Composable
fun DocenciaFormScreen(
    docentes: List<User>,
    cursos: List<Course>,
    asignaturas: List<Subject>,
    docenteId: Int?,
    cursoId: Int?,
    asignaturaId: Int?,
    isEditMode: Boolean,
    isLoading: Boolean,
    isSaving: Boolean,
    error: String?,
    onDocenteIdChange: (Int?) -> Unit,
    onCursoIdChange: (Int?) -> Unit,
    onAsignaturaIdChange: (Int?) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            LoadingState(message = "Cargando la docencia...")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                DocenciaForm(
                    docentes = docentes,
                    cursos = cursos,
                    asignaturas = asignaturas,
                    docenteId = docenteId,
                    cursoId = cursoId,
                    asignaturaId = asignaturaId,
                    onDocenteIdChange = onDocenteIdChange,
                    onCursoIdChange = onCursoIdChange,
                    onAsignaturaIdChange = onAsignaturaIdChange,
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
