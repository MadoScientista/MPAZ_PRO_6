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
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * Estado de la pantalla de inicio de sesión.
 *
 * El backend no expone un endpoint de autenticación, así que la validación se
 * resuelve contra el [UserRepository]. El ViewModel guarda el usuario que inició
 * sesión para que la navegación elija el destino inicial según su rol.
 */
class LoginViewModel(
    private val userRepository: UserRepository
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

    /**
     * Valida las credenciales.
     *
     * @param onSesionIniciada se invoca con el usuario validado.
     */
    fun iniciarSesion(onSesionIniciada: (User) -> Unit) {
        if (username.isBlank() || password.isBlank()) {
            error = "Completa el usuario y la contraseña."
            return
        }

        viewModelScope.launch {
            isLoading = true
            error = null

            userRepository.authenticate(username, password)
                .onSuccess { onSesionIniciada(it) }
                .onFailure {
                    error = (it as? ApiError)?.message
                        ?: "No se pudo iniciar sesión."
                }

            isLoading = false
        }
    }

    companion object {

        /** Factoría del ViewModel con el repositorio de usuarios. */
        fun factory(userRepository: UserRepository): ViewModelProvider.Factory =
            viewModelFactory {
                initializer { LoginViewModel(userRepository) }
            }
    }
}
