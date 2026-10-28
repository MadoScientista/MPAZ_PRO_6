package com.ccstudio.mpaz_pro_6.ui.screens.units

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitStatusChip
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitSummaryHeader

/**
 * Pantalla de autorización y publicación de una unidad.
 *
 * Permite pasar una unidad de `EN_REVISION` a `AUTORIZADO`, y de `AUTORIZADO`
 * a `PUBLICADO`. Un `BORRADOR` pertenece al docente y no se autoriza desde esta
 * pantalla. El backend exige un autorizante con rol `UTP` y una fecha de
 * autorización: una autorización incompleta devuelve `400 Bad Request`, y ese
 * error se muestra en pantalla tal cual lo reporta el backend.
 */
@Composable
fun UnitAuthorizationScreen(
    unidad: LearningUnit?,
    isLoading: Boolean,
    error: ApiError?,
    mensaje: String?,
    onAutorizar: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                isLoading && unidad == null -> LoadingState(message = "Cargando la unidad...")

                error != null && unidad == null -> ErrorState(error = error, onRetry = onRetry)

                unidad != null -> {

                    UnitSummaryHeader(
                        unit = unidad,
                        subjectName = "Unidad a revisar"
                    )

                    SectionHeader(title = "Acción disponible")

                    Text(
                        text = descripcionAccion(unidad.estado),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (error != null) {
                        Text(
                            text = error.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (mensaje != null) {
                        Text(
                            text = mensaje,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }

                    val accionHabilitada = unidad.estado == ContentStatus.IN_REVIEW ||
                            unidad.estado == ContentStatus.AUTHORIZED

                    AppButton(
                        text = when (unidad.estado) {
                            ContentStatus.IN_REVIEW -> "Autorizar unidad"
                            ContentStatus.AUTHORIZED -> "Publicar unidad"
                            ContentStatus.DRAFT -> "Unidad en borrador"
                            ContentStatus.PUBLISHED -> "Unidad publicada"
                        },
                        onClick = onAutorizar,
                        enabled = accionHabilitada && !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    EstadoActual(estado = unidad.estado)
                }
            }
        }
    }
}

/**
 * Descripción de la acción disponible según el estado de la unidad.
 */
private fun descripcionAccion(estado: ContentStatus): String = when (estado) {
    ContentStatus.DRAFT ->
        "La unidad está en borrador y pertenece al docente. Se podrá autorizar cuando la envíe a revisión."

    ContentStatus.IN_REVIEW ->
        "La unidad fue enviada a revisión. Al autorizarla podrá ser usada por el docente."

    ContentStatus.AUTHORIZED ->
        "La unidad está autorizada. Al publicarla quedará visible para los estudiantes."

    ContentStatus.PUBLISHED ->
        "La unidad ya está publicada y no admite cambios de estado."
}

/**
 * Fila con el estado actual de la unidad.
 */
@Composable
private fun EstadoActual(estado: ContentStatus) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Estado actual",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        UnitStatusChip(status = estado)
    }
}
