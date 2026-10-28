package com.ccstudio.mpaz_pro_6.ui.screens.docencias

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
import com.ccstudio.mpaz_pro_6.data.model.Docencia
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.DocenciaRepository
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Estado del listado de docencias de la UTP.
 *
 * Carga todas las docencias y los catálogos necesarios para resolver los
 * nombres. El filtro por curso es client-side y puede venir preseleccionado
 * cuando se entra desde el detalle de un curso.
 */
class DocenciaListViewModel(
    private val docenciaRepository: DocenciaRepository,
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
    private val subjectRepository: SubjectRepository,
    initialCourseId: Int? = null,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate
) : ViewModel() {

    /** Docencias registradas. */
    var docencias by mutableStateOf<List<Docencia>>(emptyList())
        private set

    /** Docentes disponibles para el formulario. */
    var docentes by mutableStateOf<List<User>>(emptyList())
        private set

    /** Cursos del establecimiento. */
    var cursos by mutableStateOf<List<Course>>(emptyList())
        private set

    /** Asignaturas del currículo. */
    var asignaturas by mutableStateOf<List<Subject>>(emptyList())
        private set

    /** Curso seleccionado para filtrar; `null` significa todos. */
    var selectedCourseId by mutableStateOf(initialCourseId)
        private set

    /** Indica que el listado se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Mensaje de la última operación de borrado. */
    var mensajeOperacion by mutableStateOf<String?>(null)
        private set

    /** Carga las docencias y sus catálogos de apoyo. */
    fun cargar() {
        viewModelScope.launch(dispatcher) {
            isLoading = true
            error = null

            val docenciasResult = docenciaRepository.list()
            if (docenciasResult.isFailure) {
                error = docenciasResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val usuariosResult = userRepository.list()
            if (usuariosResult.isFailure) {
                error = usuariosResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val cursosResult = courseRepository.list()
            if (cursosResult.isFailure) {
                error = cursosResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val asignaturasResult = subjectRepository.list()
            if (asignaturasResult.isFailure) {
                error = asignaturasResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val cursosCargados = cursosResult.getOrDefault(emptyList())
            if (selectedCourseId != null &&
                cursosCargados.none { it.id == selectedCourseId }
            ) {
                selectedCourseId = null
            }

            docencias = docenciasResult.getOrDefault(emptyList())
            docentes = usuariosResult.getOrDefault(emptyList())
                .filter { it.rol == UserRole.DOCENTE }
                .sortedBy { it.nombre.lowercase() }
            cursos = cursosCargados.sortedWith(compareBy({ it.anio }, { it.nombre }))
            asignaturas = asignaturasResult.getOrDefault(emptyList()).sortedBy { it.nombre }
            isLoading = false
        }
    }

    /** Cambia el filtro por curso. */
    fun seleccionarCurso(courseId: Int?) {
        selectedCourseId = courseId
    }

    /** Elimina una docencia y recarga el listado. */
    fun eliminar(id: Int) {
        viewModelScope.launch(dispatcher) {
            mensajeOperacion = null

            val result = docenciaRepository.delete(id)

            if (result.isFailure) {
                mensajeOperacion = mensajeEliminar(result.exceptionOrNull() as? ApiError)
                return@launch
            }

            cargar()
        }
    }

    /** Oculta el mensaje de la última operación. */
    fun limpiarMensaje() {
        mensajeOperacion = null
    }

    /** Traduce el fallo del borrado a un mensaje visible. */
    private fun mensajeEliminar(error: ApiError?): String = when (error) {
        is ApiError.Conflict ->
            "No se puede eliminar: la docencia tiene asignaciones asociadas."
        is ApiError.NotFound -> "La docencia ya no existe."
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.BadRequest -> error.message
        is ApiError.Unknown -> error.message
        null -> "No se pudo eliminar la docencia."
    }

    companion object {

        /** Factoría del ViewModel con los repositorios y el filtro inicial. */
        fun factory(
            docenciaRepository: DocenciaRepository,
            userRepository: UserRepository,
            courseRepository: CourseRepository,
            subjectRepository: SubjectRepository,
            initialCourseId: Int? = null
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                DocenciaListViewModel(
                    docenciaRepository = docenciaRepository,
                    userRepository = userRepository,
                    courseRepository = courseRepository,
                    subjectRepository = subjectRepository,
                    initialCourseId = initialCourseId
                )
            }
        }
    }
}
