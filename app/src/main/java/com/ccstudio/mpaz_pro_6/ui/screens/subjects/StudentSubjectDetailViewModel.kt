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
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.launch

/**
 * Estado del detalle de una asignatura para el estudiante.
 *
 * Muestra únicamente las unidades publicadas y asignadas a su curso. No ofrece
 * autorización ni edición.
 */
class StudentSubjectDetailViewModel(
    private val subjectRepository: SubjectRepository,
    private val unitRepository: UnitRepository,
    private val assignmentRepository: AssignmentRepository,
    private val courseId: Int?,
    private val subjectId: Int
) : ViewModel() {

    /** Asignatura consultada. */
    var subject by mutableStateOf<Subject?>(null)
        private set

    /** Unidades visibles de la asignatura. */
    var unidades by mutableStateOf<List<LearningUnit>>(emptyList())
        private set

    /** Indica que el detalle se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga la asignatura y sus unidades asignadas y publicadas. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val subjectResult = subjectRepository.getById(subjectId)
            if (subjectResult.isFailure) {
                error = subjectResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            subject = subjectResult.getOrNull()

            if (courseId == null) {
                unidades = emptyList()
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

            val unitsResult = unitRepository.list(subjectId)
            if (unitsResult.isFailure) {
                error = unitsResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            unidades = unitsResult.getOrDefault(emptyList())
                .filter {
                    it.id in assignedUnitIds &&
                        it.estado == ContentStatus.PUBLISHED
                }
                .sortedWith(compareBy({ it.orden }, { it.id }))

            isLoading = false
        }
    }

    companion object {

        /** Factoría del detalle con el curso y la asignatura. */
        fun factory(
            subjectRepository: SubjectRepository,
            unitRepository: UnitRepository,
            assignmentRepository: AssignmentRepository,
            courseId: Int?,
            subjectId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                StudentSubjectDetailViewModel(
                    subjectRepository = subjectRepository,
                    unitRepository = unitRepository,
                    assignmentRepository = assignmentRepository,
                    courseId = courseId,
                    subjectId = subjectId
                )
            }
        }
    }
}
