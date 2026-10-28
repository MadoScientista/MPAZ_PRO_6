package com.ccstudio.mpaz_pro_6.ui.screens.activities

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
import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.ui.components.activity.ActivityForm
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader

/**
 * Formulario de actividad.
 *
 * En modo creación pide `unidadId`, `titulo` y `tipo`; `contenidoId` y `orden` son
 * opcionales. El contenido, si se elige, debe ser de la misma unidad y la pantalla
 * filtra la lista al cambiar de unidad. El título es único dentro de la unidad y
 * solo se admite una `PRUEBA_FINAL` por unidad: el selector de tipo debe avisar
 * cuando la prueba final ya está en uso. La `PRUEBA_FINAL` se propone transversal.
 *
 * Cuando el formulario se abre desde el detalle de un contenido, `isContentLocked`
 * llega en `true`: el contenido queda preseleccionado y el selector se bloquea.
 * En ese caso la prueba final tampoco se ofrece, porque se gestiona fuera del
 * contenido.
 */
@Composable
fun ActivityFormScreen(
    contenidos: List<Content>,
    titulo: String,
    tipo: ActivityType,
    orden: String,
    contenidoId: Int?,
    hasFinalTest: Boolean,
    isLoading: Boolean,
    isSaving: Boolean,
    loadError: ApiError?,
    error: String?,
    onTituloChange: (String) -> Unit,
    onTipoChange: (ActivityType) -> Unit,
    onOrdenChange: (String) -> Unit,
    onContenidoChange: (Int?) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    isContentLocked: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when {
            isLoading -> LoadingState(message = "Cargando la unidad...")

            loadError != null -> ErrorState(error = loadError, onRetry = onRetry)

            else -> {
                SectionHeader(title = "Detalle de la actividad")

                Text(
                    text = if (isContentLocked) {
                        "La actividad quedará asociada al contenido desde el que la creaste."
                    } else {
                        "La actividad pertenece a esta unidad; asociarla a un contenido es opcional."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ActivityForm(
                    titulo = titulo,
                    tipo = tipo,
                    orden = orden,
                    contents = contenidos,
                    contenidoId = contenidoId,
                    onTituloChange = onTituloChange,
                    onTipoChange = onTipoChange,
                    onOrdenChange = onOrdenChange,
                    onContenidoChange = onContenidoChange,
                    onSubmit = onSubmit,
                    onCancel = onCancel,
                    isSaving = isSaving,
                    error = error,
                    hasFinalTest = hasFinalTest,
                    isContentLocked = isContentLocked
                )
            }
        }
    }
}
