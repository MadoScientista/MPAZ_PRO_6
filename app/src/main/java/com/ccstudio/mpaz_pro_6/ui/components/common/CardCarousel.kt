package com.ccstudio.mpaz_pro_6.ui.components.common

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Carrusel de tarjetas con enlace «Ver todo».
 *
 * Muestra un título con un enlace subrayado a la derecha y una `LazyRow` de
 * tarjetas del mismo ancho, con snapping. Al no usar el carrusel de Material 3
 * no hay máscara ni parallax: el contenido de cada tarjeta queda quieto y solo
 * lo recorta el borde de la pantalla. Cuando no hay ítems, muestra
 * [emptyMessage] en lugar de la lista, pero conserva el enlace.
 *
 * El componente es genérico: recibe la cantidad de ítems y un slot `item` que
 * dibuja cada tarjeta. El nombre del tag de pruebas del enlace es
 * `see-all-<title>`.
 */
@Composable
fun CardCarousel(
    title: String,
    itemCount: Int,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 16.dp,
    itemWidth: Dp = 280.dp,
    emptyMessage: String = "Sin elementos",
    item: @Composable (Int) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            TextButton(
                onClick = onSeeAll,
                modifier = Modifier.testTag("see-all-$title")
            ) {
                Text(
                    text = "Ver todo",
                    textDecoration = TextDecoration.Underline
                )
            }
        }

        if (itemCount == 0) {
            Text(
                text = emptyMessage,
                modifier = Modifier.padding(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    top = 8.dp,
                    bottom = 8.dp
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            val listState = rememberLazyListState()

            LazyRow(
                state = listState,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                items(itemCount) { index ->
                    Box(modifier = Modifier.width(itemWidth)) {
                        item(index)
                    }
                }
            }
        }
    }
}
