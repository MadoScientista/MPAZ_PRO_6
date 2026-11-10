package com.ccstudio.mpaz_pro_6

import android.os.Bundle
import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.ccstudio.mpaz_pro_6.data.local.preferences.ThemePreferenceStore
import com.ccstudio.mpaz_pro_6.ui.components.AppSwitch
import com.ccstudio.mpaz_pro_6.ui.home.HomeScreen
import com.ccstudio.mpaz_pro_6.ui.login.LoginScreen
import com.ccstudio.mpaz_pro_6.ui.login.LoginViewModel
import com.ccstudio.mpaz_pro_6.ui.splash.SplashScreen
import com.ccstudio.mpaz_pro_6.ui.theme.MPAZ_PRO_6Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    private val themePreferenceStore by lazy {
        ThemePreferenceStore(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            // Espera la preferencia guardada antes del primer frame para que el
            // splash no arranque con el tema del sistema y luego cambie.
            val initialThemeMode = themePreferenceStore.mode.first()
            setContent {
            val themeMode by themePreferenceStore.mode
                .collectAsState(initial = initialThemeMode)
            val isDarkTheme = themeMode.resolveDarkTheme(isSystemInDarkTheme())
            val scope = rememberCoroutineScope()

            val onToggleTheme: () -> Unit = {
                scope.launch {
                    themePreferenceStore.setMode(themeMode.toggled(isDarkTheme))
                }
            }

            MPAZ_PRO_6Theme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by loginViewModel.uiState.collectAsState()
                    var minSplashElapsed by rememberSaveable { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        delay(650)
                        minSplashElapsed = true
                    }

                    val currentRol = uiState.loggedInRol
                    val showSplash = !uiState.isSessionLoaded || !minSplashElapsed
                    val view = LocalView.current
                    if (!view.isInEditMode) {
                        SideEffect {
                            val window = (view.context as Activity).window
                            WindowCompat.getInsetsController(window, view)
                                .isAppearanceLightStatusBars = !isDarkTheme
                        }
                    }
                    Box(modifier = Modifier.fillMaxSize()) {
                    Crossfade(
                        targetState = currentRol,
                        animationSpec = tween(300),
                        label = "session"
                    ) { rol ->
                        if (rol != null) {
                            val drawerState = rememberDrawerState(DrawerValue.Closed)

                            // El drawer se ancla al lado de inicio (izquierda), como
                            // en el ejemplo oficial de Material 3.
                            ModalNavigationDrawer(
                                drawerState = drawerState,
                                // Material3 usa este flag tanto para el swipe como para
                                // cerrar al tocar el scrim; debe quedar en true.
                                gesturesEnabled = true,
                                drawerContent = {
                                    AppDrawerContent(
                                        drawerState = drawerState,
                                        userName = uiState.loggedInUsername ?: rol,
                                        rol = rol,
                                        isDarkTheme = isDarkTheme,
                                        onToggleTheme = onToggleTheme,
                                        onLogoutClick = {
                                            scope.launch { drawerState.close() }
                                            loginViewModel.logout()
                                        }
                                    )
                                }
                            ) {
                                HomeScreen(
                                    rol = rol,
                                    onOpenDrawer = { scope.launch { drawerState.open() } }
                                )
                            }
                        } else {
                            LoginScreen(
                                uiState = uiState,
                                isDarkTheme = isDarkTheme,
                                onToggleTheme = onToggleTheme,
                                onUsernameChange = loginViewModel::onUsernameChange,
                                onPasswordChange = loginViewModel::onPasswordChange,
                                onLoginClick = loginViewModel::login,
                                onResetLocalDataClick = loginViewModel::resetLocalData
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = showSplash,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        label = "splash"
                    ) {
                        SplashScreen(isDarkTheme = isDarkTheme)
                    }
                    }
                }
            }
        }
        }
    }
}

/**
 * Contenido del menú lateral autenticado.
 *
 * Reúne las acciones globales de la sesión: alternar el tema y cerrar sesión.
 */
@Composable
private fun AppDrawerContent(
    drawerState: DrawerState,
    userName: String,
    rol: String,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onLogoutClick: () -> Unit
) {
    BoxWithConstraints {
        // La spec de Material 3 pide 360 dp como máximo y dejar 56 dp
        // visibles de contenido al costado en un drawer modal.
        val drawerWidth = minOf(360.dp, maxWidth - 56.dp)

        ModalDrawerSheet(
            drawerState = drawerState,
            modifier = Modifier.width(drawerWidth)
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "MPAZ PRO",
                modifier = Modifier.padding(horizontal = 28.dp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = userInitials(userName),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "@$userName",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Text(
                            text = rol,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            NavigationDrawerItem(
                label = { Text("Modo oscuro") },
                selected = false,
                icon = {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                        contentDescription = null
                    )
                },
                badge = {
                    AppSwitch(
                        checked = isDarkTheme,
                        onCheckedChange = null
                    )
                },
                onClick = onToggleTheme,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            NavigationDrawerItem(
                label = { Text("Cerrar sesión") },
                selected = false,
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null
                    )
                },
                onClick = onLogoutClick,
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun userInitials(userName: String): String {
    val initials = userName
        .trim()
        .split(Regex("[\\s._-]+"))
        .mapNotNull { part -> part.firstOrNull() }
        .take(2)
        .joinToString("")

    return initials.ifBlank { userName.take(1) }.uppercase()
}
