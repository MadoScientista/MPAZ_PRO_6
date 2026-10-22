package com.ccstudio.mpaz_pro_6.ui.screens.units

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButtonVariant
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
import com.ccstudio.mpaz_pro_6.ui.components.common.ConfirmDialog
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitStatusChip
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitSummaryHeader

/**
 * Pantalla de autorización y publicación de una unidad.
 *
 * Permite pasar una unidad de `BORRADOR` o `EN_REVISION` a `AUTORIZADO`, de
 * `AUTORIZADO` a `PUBLISHED`, o devolverla a `BORRADOR` para corrección.
 * El backend exige un autorizante con rol `UTP` y una fecha de autorización para
 * `AUTORIZADO` y `PUBLISHED`.
 */
@Composable
fun UnitAuthorizationScreen(
    unidad: LearningUnit?,
    asignatura: Subject?,
    isLoading: Boolean,
    error: ApiError?,
    mensaje: String?,
    onAutorizar: () -> Unit,
    onRechazar: () -> Unit,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit = {},
    onVolverAlListado: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var mostrarConfirmarRechazo by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        AppTopBar(
            title = "Autorización de unidad",
            onNavigateBack = onNavigateBack
        )

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
                        subjectName = asignatura?.nombre ?: "Sin asignatura"
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

                    val puedeAutorizar = unidad.estado == ContentStatus.DRAFT ||
                            unidad.estado == ContentStatus.IN_REVIEW ||
                            unidad.estado == ContentStatus.AUTHORIZED

                    val puedeRechazar = unidad.estado == ContentStatus.IN_REVIEW ||
                            unidad.estado == ContentStatus.AUTHORIZED

                    if (mensaje != null) {
                        // La operación terminó: la pantalla solo ofrece el regreso.
                        AppButton(
                            text = "Volver al listado",
                            onClick = onVolverAlListado,
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        AppButton(
                            text = when (unidad.estado) {
                                ContentStatus.DRAFT, ContentStatus.IN_REVIEW -> "Autorizar unidad"
                                ContentStatus.AUTHORIZED -> "Publicar unidad"
                                ContentStatus.PUBLISHED -> "Unidad publicada"
                            },
                            onClick = onAutorizar,
                            enabled = puedeAutorizar && !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (puedeRechazar && !isLoading) {
                            AppButton(
                                text = "Rechazar y devolver a borrador",
                                onClick = { mostrarConfirmarRechazo = true },
                                enabled = true,
                                variant = AppButtonVariant.OUTLINED,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    EstadoActual(estado = unidad.estado)
                }
            }
        }
    }

    if (mostrarConfirmarRechazo) {
        ConfirmDialog(
            title = "Rechazar unidad",
            message = "¿Deseas devolver la unidad a BORRADOR para que el docente realice correcciones?",
            confirmLabel = "Rechazar",
            dismissLabel = "Cancelar",
            onConfirm = {
                mostrarConfirmarRechazo = false
                onRechazar()
            },
            onDismiss = { mostrarConfirmarRechazo = false }
        )
    }
}

/**
 * Descripción de la acción disponible según el estado de la unidad.
 */
private fun descripcionAccion(estado: ContentStatus): String = when (estado) {
    ContentStatus.DRAFT ->
        "La unidad está en borrador. Al autorizarla pasará a autorizada para uso del docente."

    ContentStatus.IN_REVIEW ->
        "La unidad fue enviada a revisión. Puedes autorizarla o devolverla a borrador para corrección."

    ContentStatus.AUTHORIZED ->
        "La unidad está autorizada. Al publicarla quedará visible para los estudiantes. También puedes devolverla a borrador."

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
