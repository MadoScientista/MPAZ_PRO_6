package com.ccstudio.mpaz_pro_6.ui.components.common

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Un destino de la barra de navegación inferior.
 *
 * `route` es la ruta del `NavHost`, `label` el texto visible y `icon` el icono
 * que muestra la barra.
 */
@Immutable
data class BottomDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)

/**
 * Barra de navegación inferior de la app.
 *
 * Recibe solo los destinos del rol actual y la ruta vigente; no conoce el
 * `NavController` ni el rol. Así el mismo componente sirve a los tres roles y
 * puede probarse con una lista cualquiera.
 */
@Composable
fun AppBottomBar(
    destinations: List<BottomDestination>,
    currentRoute: String?,
    onSelect: (BottomDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        destinations.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = { onSelect(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label
                    )
                },
                label = { Text(text = destination.label) }
            )
        }
    }
}
