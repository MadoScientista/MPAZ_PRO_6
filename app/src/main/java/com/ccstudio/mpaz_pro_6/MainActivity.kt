package com.ccstudio.mpaz_pro_6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.ccstudio.mpaz_pro_6.ui.home.HomeScreen
import com.ccstudio.mpaz_pro_6.ui.login.LoginScreen
import com.ccstudio.mpaz_pro_6.ui.login.LoginViewModel

class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val uiState by loginViewModel.uiState.collectAsState()

                    val currentRol = uiState.loggedInRol
                    if (currentRol != null) {
                        HomeScreen(
                            rol = currentRol,
                            onLogoutClick = { loginViewModel.logout() }
                        )
                    } else {
                        LoginScreen(
                            uiState = uiState,
                            onUsernameChange = loginViewModel::onUsernameChange,
                            onPasswordChange = loginViewModel::onPasswordChange,
                            onLoginClick = loginViewModel::login,
                            onResetLocalDataClick = loginViewModel::resetLocalData
                        )
                    }
                }
            }
        }
    }
}
