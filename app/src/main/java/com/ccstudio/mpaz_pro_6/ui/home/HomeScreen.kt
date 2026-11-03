package com.ccstudio.mpaz_pro_6.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ccstudio.mpaz_pro_6.ui.navigation.NavDestination
import com.ccstudio.mpaz_pro_6.ui.screens.GenericSectionScreen
import com.ccstudio.mpaz_pro_6.ui.screens.InicioScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    rol: String,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navItems = remember(rol) { NavDestination.getNavItemsForRole(rol) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavDestination.Inicio.route

    val currentTitle = navItems.find { it.route == currentRoute }?.title
        ?: when (currentRoute) {
            NavDestination.Inicio.route -> NavDestination.Inicio.title
            NavDestination.RevisionesPendientes.route -> NavDestination.RevisionesPendientes.title
            NavDestination.Cursos.route -> NavDestination.Cursos.title
            NavDestination.Docentes.route -> NavDestination.Docentes.title
            NavDestination.Usuarios.route -> NavDestination.Usuarios.title
            NavDestination.Asignaturas.route -> NavDestination.Asignaturas.title
            NavDestination.Crear.route -> NavDestination.Crear.title
            NavDestination.Informes.route -> NavDestination.Informes.title
            NavDestination.ActividadesPendientes.route -> NavDestination.ActividadesPendientes.title
            else -> "MPAZ PRO"
        }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentTitle,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            if (navItems.isNotEmpty()) {
                NavigationBar {
                    navItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavDestination.Inicio.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavDestination.Inicio.route) {
                InicioScreen(
                    rol = rol,
                    onLogoutClick = onLogoutClick
                )
            }
            composable(NavDestination.RevisionesPendientes.route) {
                GenericSectionScreen(title = NavDestination.RevisionesPendientes.title)
            }
            composable(NavDestination.Cursos.route) {
                GenericSectionScreen(title = NavDestination.Cursos.title)
            }
            composable(NavDestination.Docentes.route) {
                GenericSectionScreen(title = NavDestination.Docentes.title)
            }
            composable(NavDestination.Usuarios.route) {
                GenericSectionScreen(title = NavDestination.Usuarios.title)
            }
            composable(NavDestination.Asignaturas.route) {
                GenericSectionScreen(title = NavDestination.Asignaturas.title)
            }
            composable(NavDestination.Crear.route) {
                GenericSectionScreen(title = NavDestination.Crear.title)
            }
            composable(NavDestination.Informes.route) {
                GenericSectionScreen(title = NavDestination.Informes.title)
            }
            composable(NavDestination.ActividadesPendientes.route) {
                GenericSectionScreen(title = NavDestination.ActividadesPendientes.title)
            }
        }
    }
}
