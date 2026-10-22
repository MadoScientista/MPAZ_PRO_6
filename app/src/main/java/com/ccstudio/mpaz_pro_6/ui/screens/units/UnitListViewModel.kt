package com.ccstudio.mpaz_pro_6.ui.screens.units

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.launch

/**
 * Estado del listado de unidades.
 *
 * El mismo listado sirve a los tres roles y cambia su contenido según el
 * `UserRole`: la UTP ve las unidades `BORRADOR` y `EN_REVISION` pendientes de
 * revisión, el docente las `AUTORIZADO` y `PUBLICADO`, y el estudiante solo las
 * `PUBLICADO`.
 *
 * La búsqueda por texto libre es **client-side**: el backend no la expone.
 */
class UnitListViewModel(
    private val unitRepository: UnitRepository,
    private val subjectRepository: SubjectRepository,
    private val rol: UserRole
) : ViewModel() {

    /** Unidades visibles según el rol del usuario. */
    var unidades by mutableStateOf<List<LearningUnit>>(emptyList())
        private set

    /** Asignaturas, para resolver el nombre que la unidad solo trae por id. */
    var asignaturas by mutableStateOf<List<Subject>>(emptyList())
        private set

    /** Indica que el listado se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga las unidades y las asignaturas. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val unidadesResult = unitRepository.list()

            if (unidadesResult.isFailure) {
                error = unidadesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val asignaturasResult = subjectRepository.list()

            if (asignaturasResult.isFailure) {
                error = asignaturasResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            unidades = unidadesResult.getOrDefault(emptyList())
                .filter { esVisiblePara(it) }

            asignaturas = asignaturasResult.getOrDefault(emptyList())

            isLoading = false
        }
    }

    /**
     * Indica si la unidad debe mostrarse al usuario con el rol de la pantalla.
     */
    private fun esVisiblePara(unit: LearningUnit): Boolean = when (rol) {
        UserRole.UTP -> unit.estado.esPendienteDeRevision()
        UserRole.DOCENTE -> unit.estado == ContentStatus.AUTHORIZED ||
                unit.estado == ContentStatus.PUBLISHED
        UserRole.ESTUDIANTE -> unit.estado == ContentStatus.PUBLISHED
    }

    companion object {

        /** Factoría del ViewModel con los repositorios del listado. */
        fun factory(
            unitRepository: UnitRepository,
            subjectRepository: SubjectRepository,
            rol: UserRole
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UnitListViewModel(unitRepository, subjectRepository, rol)
            }
        }
    }
}
