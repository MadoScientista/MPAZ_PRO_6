package com.ccstudio.mpaz_pro_6.ui.screens.subjects

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.launch

/**
 * Estado del listado de asignaturas del estudiante.
 *
 * Solo expone las asignaturas que tienen al menos una unidad publicada y
 * asignada al curso del estudiante. No permite crear, editar ni borrar.
 */
class StudentSubjectListViewModel(
    private val subjectRepository: SubjectRepository,
    private val unitRepository: UnitRepository,
    private val assignmentRepository: AssignmentRepository,
    private val courseId: Int?
) : ViewModel() {

    /** Asignaturas visibles para el estudiante, ordenadas por nombre. */
    var asignaturas by mutableStateOf<List<Subject>>(emptyList())
        private set

    /** Cantidad de unidades visibles por asignatura. */
    var estadisticasPorAsignatura by mutableStateOf<Map<Int, SubjectStats>>(emptyMap())
        private set

    /** `true` cuando el estudiante no tiene curso asignado. */
    var sinCurso by mutableStateOf(false)
        private set

    /** Indica que el listado se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga las asignaturas con unidades asignadas y publicadas. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null
            sinCurso = false

            if (courseId == null) {
                asignaturas = emptyList()
                estadisticasPorAsignatura = emptyMap()
                sinCurso = true
                isLoading = false
                return@launch
            }

            val subjectsResult = subjectRepository.list()
            if (subjectsResult.isFailure) {
                error = subjectsResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val unitsResult = unitRepository.list()
            if (unitsResult.isFailure) {
                error = unitsResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val assignmentsResult = assignmentRepository.list(courseId = courseId)
            if (assignmentsResult.isFailure) {
                error = assignmentsResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val assignedUnitIds = assignmentsResult
                .getOrDefault(emptyList())
                .map { it.unidadId }
                .toSet()

            val visibleUnits = unitsResult.getOrDefault(emptyList())
                .filter {
                    it.id in assignedUnitIds &&
                        it.estado == ContentStatus.PUBLISHED
                }

            val subjectIds = visibleUnits.map { it.asignaturaId }.toSet()
            asignaturas = subjectsResult.getOrDefault(emptyList())
                .filter { it.id in subjectIds }
                .sortedBy { it.nombre.lowercase() }

            estadisticasPorAsignatura = visibleUnits
                .groupBy { it.asignaturaId }
                .mapValues { (_, units) ->
                    SubjectStats(total = units.size, pending = 0)
                }

            isLoading = false
        }
    }

    companion object {

        /** Factoría del listado con el curso del estudiante. */
        fun factory(
            subjectRepository: SubjectRepository,
            unitRepository: UnitRepository,
            assignmentRepository: AssignmentRepository,
            courseId: Int?
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                StudentSubjectListViewModel(
                    subjectRepository = subjectRepository,
                    unitRepository = unitRepository,
                    assignmentRepository = assignmentRepository,
                    courseId = courseId
                )
            }
        }
    }
}
