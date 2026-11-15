package com.ccstudio.mpaz_pro_6.data

import android.content.Context
import com.ccstudio.mpaz_pro_6.BuildConfig
import com.ccstudio.mpaz_pro_6.data.local.MpazDatabase
import com.ccstudio.mpaz_pro_6.data.local.preferences.SessionManager
import com.ccstudio.mpaz_pro_6.data.remote.ApiClient
import com.ccstudio.mpaz_pro_6.data.remote.InitialSync
import com.ccstudio.mpaz_pro_6.data.remote.MpazApi
import com.ccstudio.mpaz_pro_6.data.remote.RemoteAuthRepository
import com.ccstudio.mpaz_pro_6.data.remote.RemoteCatalogRepository
import com.ccstudio.mpaz_pro_6.data.remote.RemoteEvaluationRepository
import com.ccstudio.mpaz_pro_6.data.dto.identidad.UsuarioResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Contenedor manual de dependencias del modo remoto.
 *
 * <p>La app puede seguir funcionando en modo local si
 * `MPAZ_LOCAL_ONLY=true`. En modo remoto, Room actua como cache y el backend
 * es la fuente de verdad.</p>
 */
class AppContainer(context: Context) {

    val localOnly: Boolean = BuildConfig.MPAZ_LOCAL_ONLY

    val database: MpazDatabase = MpazDatabase.getDatabase(context)

    val sessionManager = SessionManager(context)

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val apiClient = ApiClient(
        tokenProvider = sessionManager::currentToken,
        onUnauthorized = {
            appScope.launch { sessionManager.clearSession() }
        }
    )

    val api: MpazApi = apiClient.api

    val remoteAuth = RemoteAuthRepository(api, sessionManager)

    val remoteEvaluation = RemoteEvaluationRepository(api)

    val remoteCatalog = RemoteCatalogRepository(api)

    val initialSync = InitialSync(api, database)

    private val _syncCompleted = MutableStateFlow(0L)

    /**
     * Emite una nueva version cada vez que Room termina de repoblarse. Las
     * pantallas que ya se mostraron con la cache anterior pueden recargar sin
     * esperar a que el usuario navegue.
     */
    val syncCompleted: StateFlow<Long> = _syncCompleted.asStateFlow()

    fun notifySyncCompleted() {
        _syncCompleted.value += 1
    }

    /** Ultimo usuario autenticado, usado para reconstruir la cache Room. */
    @Volatile
    var currentUsuario: UsuarioResponse? = null

    /** Vuelve a sincronizar Room desde el backend. */
    suspend fun refresh(): Result<Unit> {
        val usuario = currentUsuario
            ?: return Result.failure(IllegalStateException("No hay usuario autenticado"))
        val result = initialSync.sync(usuario)
        if (result.isSuccess) {
            notifySyncCompleted()
        }
        return result
    }
}
