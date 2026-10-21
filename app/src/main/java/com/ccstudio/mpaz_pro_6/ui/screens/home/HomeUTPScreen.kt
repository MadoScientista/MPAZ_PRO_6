package com.ccstudio.mpaz_pro_6.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
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
    userName: String,
    unidadesPendientes: Int,
    totalCursos: Int,
    totalAsignaturas: Int,
    onVerUnidades: () -> Unit,
    onCerrarSesion: () -> Unit,
    onCargar: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        AppTopBar(
            title = "Panel de UTP",
            actions = {
                TextButton(onClick = onCerrarSesion) {
                    Text(text = "Salir")
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (userName.isBlank()) "Hola" else "Hola, $userName",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            when {
                isLoading -> LoadingState(message = "Cargando el resumen...")

                error != null -> ErrorState(error = error, onRetry = onCargar)

                else -> {

                    SectionHeader(title = "Resumen")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ContadorCard(
                            titulo = "Pendientes",
                            valor = unidadesPendientes,
                            modifier = Modifier.weight(1f)
                        )

                        ContadorCard(
                            titulo = "Cursos",
                            valor = totalCursos,
                            modifier = Modifier.weight(1f)
                        )

                        ContadorCard(
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

/**
 * Tarjeta con un contador del resumen.
 */
@Composable
private fun ContadorCard(
    titulo: String,
    valor: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = valor.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = titulo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
