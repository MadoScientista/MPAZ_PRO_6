package com.ccstudio.mpaz_pro_6.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ccstudio.mpaz_pro_6.MpazApplication
import com.ccstudio.mpaz_pro_6.data.local.MpazDatabase
import com.ccstudio.mpaz_pro_6.data.local.preferences.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedInRol: String? = null,
    val loggedInUserId: Long? = null,
    val loggedInUsername: String? = null,
    val resetMessage: String? = null,
    val isSessionLoaded: Boolean = false,
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as MpazApplication).container
    private val db: MpazDatabase = container.database
    private val identidadDao = db.identidadDao()
    private val sessionManager: SessionManager = container.sessionManager
    private val localOnly: Boolean = container.localOnly

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Un login nuevo debe esperar al sync inicial para no mostrar el Home con
     * la cache anterior. En cambio, una sesion restaurada al arrancar debe
     * mostrarse de inmediato, aunque el backend no responda.
     */
    @Volatile
    private var suppressSessionUpdates = false

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Primero se restaura el token en memoria para que cualquier
            // pantalla que consulte la cache pueda autenticarse si hay red.
            if (!localOnly) {
                sessionManager.restoreToken()
            }
            sessionManager.userSessionFlow.collect { session ->
                if (!suppressSessionUpdates) {
                    _uiState.update {
                        it.copy(
                            loggedInRol = session?.rol,
                            loggedInUserId = session?.userId,
                            loggedInUsername = session?.username,
                            isSessionLoaded = true,
                        )
                    }
                }
            }
        }

        if (!localOnly) {
            bootstrapRemoteSession()
        }
    }

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

        if (localOnly) {
            loginLocal(currentUsername, currentPassword)
        } else {
            loginRemote(currentUsername, currentPassword)
        }
    }

    private fun loginLocal(currentUsername: String, currentPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = identidadDao.getUsuarioByNombre(currentUsername)
            if (user == null || user.passwordHash != currentPassword) {
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Usuario o contraseña incorrectos",
                        )
                    }
                }
                return@launch
            }

            if (!user.activo) {
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "El usuario se encuentra inactivo",
                        )
                    }
                }
                return@launch
            }

            val rol = identidadDao.getRolById(user.rolId)
            val rolNombre = rol?.nombre ?: "USUARIO"

            sessionManager.saveSession(
                userId = user.id,
                username = user.nombreUsuario,
                rol = rolNombre,
            )

            withContext(Dispatchers.Main) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loggedInRol = rolNombre,
                        loggedInUserId = user.id,
                        loggedInUsername = user.nombreUsuario,
                        errorMessage = null,
                    )
                }
            }
        }
    }

    private fun loginRemote(currentUsername: String, currentPassword: String) {
        suppressSessionUpdates = true
        viewModelScope.launch(Dispatchers.IO) {
            val result = container.remoteAuth.login(currentUsername, currentPassword)

            result.fold(
                onSuccess = { remote ->
                    container.currentUsuario = remote.usuario
                    val sync = container.initialSync.sync(remote.usuario)
                    withContext(Dispatchers.Main) {
                        suppressSessionUpdates = false
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                loggedInRol = remote.session.rol,
                                loggedInUserId = remote.session.userId,
                                loggedInUsername = remote.session.username,
                                isSessionLoaded = true,
                                errorMessage = sync.exceptionOrNull()?.let { error ->
                                    "Sesión iniciada, pero no se pudo sincronizar: ${error.message}"
                                },
                            )
                        }
                    }
                },
                onFailure = { error ->
                    withContext(Dispatchers.Main) {
                        suppressSessionUpdates = false
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isSessionLoaded = true,
                                errorMessage = error.message ?: "No se pudo iniciar sesión",
                            )
                        }
                    }
                }
            )
        }
    }

    private fun bootstrapRemoteSession() {
        viewModelScope.launch(Dispatchers.IO) {
            // La sesion local ya se mostro en la UI. Si no hay red se conserva
            // la cache; si el backend devuelve 401/403, RemoteAuthRepository
            // limpia la sesion y el flujo vuelve al login.
            container.remoteAuth.bootstrap()
                .onSuccess { remote ->
                    container.currentUsuario = remote.usuario
                    container.initialSync.sync(remote.usuario)
                }
        }
    }

    fun resetLocalData() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, resetMessage = null) }
        viewModelScope.launch(Dispatchers.IO) {
            sessionManager.clearSession()
            if (localOnly) {
                db.resetDatabase()
            } else {
                db.clearAllTables()
            }
            withContext(Dispatchers.Main) {
                _uiState.update {
                    it.copy(
                        username = "",
                        password = "",
                        isLoading = false,
                        errorMessage = null,
                        resetMessage = "Datos locales reseteados con éxito",
                    )
                }
            }
        }
    }

    fun logout() {
        if (localOnly) {
            viewModelScope.launch { sessionManager.clearSession() }
            _uiState.update { LoginUiState(isSessionLoaded = true) }
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            container.remoteAuth.logout()
            withContext(Dispatchers.Main) {
                _uiState.update { LoginUiState(isSessionLoaded = true) }
            }
        }
    }
}
