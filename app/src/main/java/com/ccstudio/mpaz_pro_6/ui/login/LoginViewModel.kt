package com.ccstudio.mpaz_pro_6.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ccstudio.mpaz_pro_6.data.local.MpazDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedInRol: String? = null,
    val resetMessage: String? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MpazDatabase.getDatabase(application)
    private val identidadDao = db.identidadDao()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsernameChange(newUsername: String) {
        _uiState.update { it.copy(username = newUsername, errorMessage = null, resetMessage = null) }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = null, resetMessage = null) }
    }

    fun login() {
        val currentUsername = _uiState.value.username.trim()
        val currentPassword = _uiState.value.password

        if (currentUsername.isEmpty() || currentPassword.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Ingrese usuario y contraseña") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, resetMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            val user = identidadDao.getUsuarioByNombre(currentUsername)
            if (user == null || user.passwordHash != currentPassword) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Usuario o contraseña incorrectos"
                    )
                }
                return@launch
            }

            if (!user.activo) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "El usuario se encuentra inactivo"
                    )
                }
                return@launch
            }

            val rol = identidadDao.getRolById(user.rolId)
            val rolNombre = rol?.nombre ?: "USUARIO"

            _uiState.update {
                it.copy(
                    isLoading = false,
                    loggedInRol = rolNombre,
                    errorMessage = null
                )
            }
        }
    }

    fun resetLocalData() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, resetMessage = null) }
        viewModelScope.launch(Dispatchers.IO) {
            db.resetDatabase()
            _uiState.update {
                it.copy(
                    username = "",
                    password = "",
                    isLoading = false,
                    errorMessage = null,
                    resetMessage = "Datos locales reseteados con éxito"
                )
            }
        }
    }

    fun logout() {
        _uiState.update {
            LoginUiState()
        }
    }
}
