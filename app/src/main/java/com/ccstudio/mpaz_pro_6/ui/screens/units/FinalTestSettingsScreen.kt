package com.ccstudio.mpaz_pro_6.ui.screens.units

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.ui.components.activity.FinalTestForm
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import java.time.Instant

/**
 * Configuración de la prueba final de una unidad.
 *
 * Permite al docente modificar la fecha de aplicación, el número máximo de
 * intentos y el estado activado/desactivado de la prueba final. El ViewModel
 * envía únicamente los campos modificados.
 */
@Composable
fun FinalTestSettingsScreen(
    fecha: Instant?,
    maxIntentos: String,
    pruebaFinalHabilitada: Boolean,
    isLoading: Boolean,
    isSaving: Boolean,
    error: String?,
    onFechaChange: (Instant?) -> Unit,
    onMaxIntentosChange: (String) -> Unit,
    onPruebaFinalHabilitadaChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            LoadingState(message = "Cargando la prueba final...")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                FinalTestForm(
                    fecha = fecha,
                    maxIntentos = maxIntentos,
                    pruebaFinalHabilitada = pruebaFinalHabilitada,
                    onFechaChange = onFechaChange,
                    onMaxIntentosChange = onMaxIntentosChange,
                    onPruebaFinalHabilitadaChange = onPruebaFinalHabilitadaChange,
                    onSubmit = onSubmit,
                    onCancel = onCancel,
                    isSaving = isSaving,
                    error = error
                )
            }
        }
    }
}
