package com.ccstudio.mpaz_pro_6.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.repository.AuthRepository
import kotlinx.coroutines.launch

/**
 * Estado de la pantalla de inicio de sesión.
 *
 * Valida las credenciales contra `/api/auth/login`. Cuando el repositorio guarda
 * la sesión, la navegación observa `Session` y cambia al panel del rol.
 */
class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    /** Nombre de usuario escrito en el formulario. */
    var username by mutableStateOf("")
        private set

    /** Contraseña escrita en el formulario. */
    var password by mutableStateOf("")
        private set

    /** Indica que la validación está en curso. */
    var isLoading by mutableStateOf(false)
        private set

    /** Mensaje de error de la última validación. */
    var error by mutableStateOf<String?>(null)
        private set

    /** Actualiza el nombre de usuario y limpia el error anterior. */
    fun onUsernameChange(value: String) {
        username = value
        error = null
    }

    /** Actualiza la contraseña y limpia el error anterior. */
    fun onPasswordChange(value: String) {
        password = value
        error = null
    }

    /** Envía las credenciales al backend. */
    fun iniciarSesion() {
        if (username.isBlank() || password.isBlank()) {
            error = "Completa el usuario y la contraseña."
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null

            authRepository.login(username, password)
                .onFailure {
                    error = (it as? ApiError)?.message
                        ?: "No se pudo iniciar sesión."
                }

            isLoading = false
        }
    }

    companion object {

        /** Factoría del ViewModel con el repositorio de autenticación. */
        fun factory(authRepository: AuthRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { LoginViewModel(authRepository) }
            }
    }
}
