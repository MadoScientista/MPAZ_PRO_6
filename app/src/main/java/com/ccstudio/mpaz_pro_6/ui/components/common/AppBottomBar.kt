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
 * Recibe solo los destinos del rol actual y la ruta que debe aparecer
 * seleccionada; no conoce el `NavController` ni el rol. En un detalle hijo, la
 * ruta seleccionada es la pestaña padre, para que la sección siga marcada. Así
 * el mismo componente sirve a los tres roles y puede probarse con una lista
 * cualquiera.
 */
@Composable
fun AppBottomBar(
    destinations: List<BottomDestination>,
    selectedRoute: String?,
    onSelect: (BottomDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        destinations.forEach { destination ->
            NavigationBarItem(
                selected = selectedRoute == destination.route,
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
