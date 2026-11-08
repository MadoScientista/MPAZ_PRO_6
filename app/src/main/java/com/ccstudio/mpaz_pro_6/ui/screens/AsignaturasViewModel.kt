package com.ccstudio.mpaz_pro_6.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ccstudio.mpaz_pro_6.data.local.MpazDatabase
import com.ccstudio.mpaz_pro_6.data.local.dao.AsignaturaCursoDetalle
import com.ccstudio.mpaz_pro_6.data.local.preferences.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class AsignaturasUiState(
    val isLoading: Boolean = true,
    val asignaturas: List<AsignaturaCursoDetalle> = emptyList(),
    val searchQuery: String = "",
    val userRol: String = "",
)

class AsignaturasViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MpazDatabase.getDatabase(application)
    private val docenciaDao = db.docenciaDao()
    private val sessionManager = SessionManager(application)

    private val _asignaturasState = MutableStateFlow(AsignaturasUiState())
    val asignaturasState: StateFlow<AsignaturasUiState> = _asignaturasState.asStateFlow()

    fun loadAsignaturas() {
        viewModelScope.launch {
            _asignaturasState.value = _asignaturasState.value.copy(isLoading = true)
            val session = sessionManager.userSessionFlow.firstOrNull()
            val userId = session?.userId
            val rol = session?.rol ?: ""

            val asignaturas = if (userId != null) {
                when (rol.uppercase()) {
                    "UTP" -> docenciaDao.getAllAsignaturasCursoDetalles()
                    "DOCENTE" -> docenciaDao.getAsignaturasCursoByDocenteId(userId)
                    "ESTUDIANTE" -> docenciaDao.getAsignaturasCursoByEstudianteId(userId)
                    else -> emptyList()
                }
            } else {
                emptyList()
            }

            _asignaturasState.value = _asignaturasState.value.copy(
                isLoading = false,
                asignaturas = asignaturas,
                userRol = rol,
            )
        }
    }

    fun updateSearchQuery(query: String) {
        _asignaturasState.value = _asignaturasState.value.copy(searchQuery = query)
    }
}
