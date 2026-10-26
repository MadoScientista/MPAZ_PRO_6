package com.ccstudio.mpaz_pro_6.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.ui.theme.LocalTopBarColors

/**
 * Barra superior de la app.
 *
 * La monta el `Scaffold` de `AppNavGraph`, no las pantallas: así queda fija
 * mientras el contenido cambia de destino, igual que la barra inferior.
 *
 * El título es un slot: en los destinos de nivel superior (pestañas) recibe la
 * cabecera de perfil y en el resto, el título del destino. La altura es fija
 * (72 dp, una decisión de marca por encima del tamaño small de Material 3, que
 * son 64 dp) para que el área de contenido no cambie entre destinos.
 *
 * No usa `TopAppBar` de Material porque ese componente anima su color de fondo
 * al cambiar de tema; con `Surface` el cambio es inmediato y acompaña al resto
 * de la app. Toma los colores de [LocalTopBarColors].
 */
@Composable
fun AppTopBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val barColors = LocalTopBarColors.current
    val containerColor = barColors.container.takeOrElse {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = barColors.content.takeOrElse {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        color = containerColor,
        contentColor = contentColor,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .height(72.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onNavigateBack != null) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                title()
            }

            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                content = actions
            )
        }
    }
}
