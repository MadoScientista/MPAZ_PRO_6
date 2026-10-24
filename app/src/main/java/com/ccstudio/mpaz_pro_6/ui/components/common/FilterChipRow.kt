package com.ccstudio.mpaz_pro_6.ui.components.common

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Fila de chips de filtro.
 *
 * Muestra en horizontal los filtros activos de un listado, como el filtro por
 * asignatura del listado de unidades.
 *
 * `disabledOptions` permite bloquear opciones concretas sin quitarlas de la fila,
 * para que la pantalla pueda explicar por qué no están disponibles (por ejemplo,
 * una segunda `PRUEBA_FINAL` en una unidad).
 */
@Composable
fun FilterChipRow(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    disabledOptions: Set<String> = emptySet()
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(end = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(text = option) },
                enabled = option !in disabledOptions
            )
        }
    }
}
