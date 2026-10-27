package com.ccstudio.mpaz_pro_6.ui.screens.home

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
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import com.ccstudio.mpaz_pro_6.data.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Estado del panel de inicio de la jefa de UTP.
 *
 * El backend no expone un listado dedicado de unidades pendientes de revisión:
 * el panel expone las que están en `BORRADOR` o `EN_REVISION` sobre
 * `GET /api/unidades`, junto con todas las asignaturas y los contadores.
 */
class HomeUTPViewModel(
    private val unitRepository: UnitRepository,
    private val courseRepository: CourseRepository,
    private val subjectRepository: SubjectRepository,
    private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate
) : ViewModel() {

    /** Indica que el panel se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Unidades pendientes de revisión, para el carrusel del panel. */
    var unidadesPendientes by mutableStateOf<List<LearningUnit>>(emptyList())
        private set

    /** Cursos registrados, para el carrusel del panel. */
    var cursos by mutableStateOf<List<Course>>(emptyList())
        private set

    /** Cantidad de estudiantes por curso, para la tarjeta del carrusel. */
    var estudiantesPorCurso by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set

    /** Asignaturas registradas, para el carrusel del panel. */
    var asignaturas by mutableStateOf<List<Subject>>(emptyList())
        private set

    /** Cantidad total de unidades de cada asignatura, para el carrusel. */
    var unidadesPorAsignatura by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set

    /** Carga las listas y los contadores del panel. */
    fun cargar(): Job = viewModelScope.launch(dispatcher) {
        isLoading = true
        error = null

        val units = unitRepository.list()

        if (units.isFailure) {
            error = units.exceptionOrNull() as? ApiError
            isLoading = false
            return@launch
        }

        val courses = courseRepository.list()

        if (courses.isFailure) {
            error = courses.exceptionOrNull() as? ApiError
            isLoading = false
            return@launch
        }

        val subjects = subjectRepository.list()

        if (subjects.isFailure) {
            error = subjects.exceptionOrNull() as? ApiError
            isLoading = false
            return@launch
        }

        val users = userRepository.list()

        if (users.isFailure) {
            error = users.exceptionOrNull() as? ApiError
            isLoading = false
            return@launch
        }

        val allUnits = units.getOrDefault(emptyList())

        unidadesPendientes = allUnits.filter { it.estado.esPendienteDeRevision() }
        unidadesPorAsignatura = allUnits
            .groupingBy { it.asignaturaId }
            .eachCount()

        cursos = courses.getOrDefault(emptyList())
        asignaturas = subjects.getOrDefault(emptyList())
        estudiantesPorCurso = users.getOrDefault(emptyList())
            .filter { it.rol == UserRole.ESTUDIANTE && it.cursoId != null }
            .groupBy { it.cursoId!! }
            .mapValues { (_, students) -> students.size }

        isLoading = false
    }

    companion object {

        /** Factoría del ViewModel con los repositorios del panel. */
        fun factory(
            unitRepository: UnitRepository,
            courseRepository: CourseRepository,
            subjectRepository: SubjectRepository,
            userRepository: UserRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeUTPViewModel(
                    unitRepository = unitRepository,
                    courseRepository = courseRepository,
                    subjectRepository = subjectRepository,
                    userRepository = userRepository
                )
            }
        }
    }
}
