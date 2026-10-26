package com.ccstudio.mpaz_pro_6.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.CounterCard
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader

/**
 * Panel de inicio de la jefa de UTP.
 *
 * Muestra un resumen de la gestión: unidades `BORRADOR` pendientes de revisión,
 * total de cursos y asignaturas, y accesos directos a la gestión de unidades.
 *
 * Las unidades pendientes se obtienen filtrando por `BORRADOR` o `EN_REVISION`
 * sobre `GET /api/unidades`, porque el backend no expone un listado dedicado.
 */
@Composable
fun HomeUTPScreen(
    unidadesPendientes: Int,
    totalCursos: Int,
    totalAsignaturas: Int,
    onVerUnidades: () -> Unit,
    onCargar: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
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
                isLoading && unidadesPendientes == 0 && totalCursos == 0 && totalAsignaturas == 0 ->
                    LoadingState(message = "Cargando el resumen...")

                error != null -> ErrorState(error = error, onRetry = onCargar)

                else -> {

                    SectionHeader(title = "Resumen")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CounterCard(
                            titulo = "Pendientes",
                            valor = unidadesPendientes,
                            modifier = Modifier.weight(1f)
                        )

                        CounterCard(
                            titulo = "Cursos",
                            valor = totalCursos,
                            modifier = Modifier.weight(1f)
                        )

                        CounterCard(
                            titulo = "Asignaturas",
                            valor = totalAsignaturas,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    SectionHeader(title = "Gestión")

                    AppButton(
                        text = "Revisar unidades pendientes",
                        onClick = onVerUnidades
                    )
                }
            }
        }
    }
}

