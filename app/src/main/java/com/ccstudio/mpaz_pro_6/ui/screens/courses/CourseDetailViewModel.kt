package com.ccstudio.mpaz_pro_6.ui.screens.courses

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import com.ccstudio.mpaz_pro_6.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * Estado del detalle de un curso.
 *
 * Carga el curso con `GET /api/cursos/{id}`, sus estudiantes con
 * `GET /api/usuarios` (filtrados por rol `ESTUDIANTE` y `cursoId`), sus
 * asignaciones con `GET /api/asignaciones?cursoId={id}` para resolver las
 * unidades asociadas al curso y sus asignaturas con `GET /api/asignaturas`.
 */
class CourseDetailViewModel(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val assignmentRepository: AssignmentRepository,
    private val unitRepository: UnitRepository,
    private val subjectRepository: SubjectRepository,
    private val courseId: Int
) : ViewModel() {

    /** Curso cargado, o `null` mientras carga o si falló. */
    var course by mutableStateOf<Course?>(null)
        private set

    /** Asignaturas del curso. */
    var asignaturas by mutableStateOf<List<Subject>>(emptyList())
        private set

    /** Cantidad de unidades por asignatura en este curso. */
    var unidadesPorAsignatura by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set

    /** Estudiantes del curso, ordenados por nombre. */
    var estudiantes by mutableStateOf<List<User>>(emptyList())
        private set

    /** Indica que el detalle se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga el curso, sus estudiantes y sus asignaturas. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val courseResult = courseRepository.getById(courseId)

            if (courseResult.isFailure) {
                error = courseResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            course = courseResult.getOrNull()

            val usersResult = userRepository.list()

            if (usersResult.isFailure) {
                error = usersResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            estudiantes = usersResult.getOrDefault(emptyList())
                .filter { it.rol == UserRole.ESTUDIANTE && it.cursoId == courseId }
                .sortedBy { it.nombre.lowercase() }

            val assignmentsResult = assignmentRepository.list(courseId = courseId)
            val unitsResult = unitRepository.list()
            val subjectsResult = subjectRepository.list()

            val assignments = assignmentsResult.getOrDefault(emptyList())
            val unitIdsForCourse = assignments.map { it.unidadId }.toSet()

            val allUnits = unitsResult.getOrDefault(emptyList())
            val unitsForCourse = if (unitIdsForCourse.isNotEmpty()) {
                allUnits.filter { it.id in unitIdsForCourse }
            } else {
                emptyList()
            }

            val subjectIdsForCourse = unitsForCourse.map { it.asignaturaId }.toSet()
            val allSubjects = subjectsResult.getOrDefault(emptyList())

            asignaturas = if (subjectIdsForCourse.isNotEmpty()) {
                allSubjects.filter { it.id in subjectIdsForCourse }.sortedBy { it.nombre.lowercase() }
            } else {
                emptyList()
            }

            unidadesPorAsignatura = unitsForCourse
                .groupingBy { it.asignaturaId }
                .eachCount()

            isLoading = false
        }
    }

    companion object {

        /** Factoría del ViewModel con los repositorios y el id del curso. */
        fun factory(
            courseRepository: CourseRepository,
            userRepository: UserRepository,
            assignmentRepository: AssignmentRepository,
            unitRepository: UnitRepository,
            subjectRepository: SubjectRepository,
            courseId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CourseDetailViewModel(
                    courseRepository = courseRepository,
                    userRepository = userRepository,
                    assignmentRepository = assignmentRepository,
                    unitRepository = unitRepository,
                    subjectRepository = subjectRepository,
                    courseId = courseId
                )
            }
        }
    }
}
