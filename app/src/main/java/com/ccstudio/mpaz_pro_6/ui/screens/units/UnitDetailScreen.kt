package com.ccstudio.mpaz_pro_6.ui.screens.units

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.common.ThemeToggleButton
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitSummaryHeader

/**
 * Detalle de una unidad.
 *
 * Consume `GET /api/unidades/{id}` y muestra los datos de la unidad con acceso a
 * la pantalla de autorización.
 *
 * El agrupado en objetivos de aprendizaje, contenidos y actividades llega cuando
 * existan sus repositorios; por ahora la pantalla muestra la unidad y su estado.
 *
 * Las acciones disponibles dependen del `ContentStatus` y del rol del usuario: en
 * `BORRADOR` y `EN_REVISION` la UTP autoriza, en `AUTHORIZED` la UTP publica, y en
 * `PUBLISHED` todos solo leen.
 */
@Composable
fun UnitDetailScreen(
    unidad: LearningUnit?,
    asignatura: Subject?,
    isLoading: Boolean,
    error: ApiError?,
    onAutorizar: () -> Unit,
    onRetry: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        AppTopBar(
            title = "Detalle de la unidad",
            onNavigateBack = onNavigateBack,
            actions = {
                ThemeToggleButton(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                isLoading -> LoadingState(message = "Cargando la unidad...")

                error != null -> ErrorState(error = error, onRetry = onRetry)

                unidad != null -> {

                    UnitSummaryHeader(
                        unit = unidad,
                        subjectName = asignatura?.nombre ?: "Sin asignatura"
                    )

                    SectionHeader(title = "Datos de la unidad")

                    DatoLinea(etiqueta = "Asignatura", valor = asignatura?.nombre ?: "-")
                    DatoLinea(etiqueta = "Nivel educativo", valor = unidad.nivelEducativo)
                    DatoLinea(etiqueta = "Título", valor = unidad.titulo)
                    DatoLinea(etiqueta = "Orden", valor = unidad.orden.toString())
                    DatoLinea(etiqueta = "Estado", valor = unidad.estado.wireValue)
                    DatoLinea(
                        etiqueta = "Autorizada por",
                        valor = unidad.autorizadaPorId?.let { "Usuario $it" } ?: "-"
                    )
                    DatoLinea(
                        etiqueta = "Fecha de autorización",
                        valor = unidad.fechaAutorizacion?.toString() ?: "-"
                    )

                    if (unidad.estado.esPendienteDeRevision() ||
                        unidad.estado == ContentStatus.AUTHORIZED
                    ) {
                        AppButton(
                            text = if (unidad.estado == ContentStatus.AUTHORIZED) {
                                "Publicar unidad"
                            } else {
                                "Autorizar unidad"
                            },
                            onClick = onAutorizar,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Línea de dato con su etiqueta.
 */
@Composable
private fun DatoLinea(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
