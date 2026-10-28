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
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateDocenciaRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateDocenciaRequest
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.DocenciaRepository
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Estado del formulario de docencia.
 *
 * Sin `docenciaId` crea; con id edita. En edición solo se envía el docente
 * porque el `PUT` de docencias no admite cambiar curso ni asignatura.
 */
class DocenciaFormViewModel(
    private val docenciaRepository: DocenciaRepository,
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
    private val subjectRepository: SubjectRepository,
    private val docenciaId: Int?,
    private val initialCourseId: Int? = null,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate
) : ViewModel() {

    /** Docentes disponibles. */
    var docentes by mutableStateOf<List<User>>(emptyList())
        private set

    /** Cursos disponibles. */
    var cursos by mutableStateOf<List<Course>>(emptyList())
        private set

    /** Asignaturas disponibles. */
    var asignaturas by mutableStateOf<List<Subject>>(emptyList())
        private set

    /** Docente seleccionado. */
    var docenteId by mutableStateOf<Int?>(null)
        private set

    /** Curso seleccionado. */
    var cursoId by mutableStateOf<Int?>(null)
        private set

    /** Asignatura seleccionada. */
    var asignaturaId by mutableStateOf<Int?>(null)
        private set

    /** Indica que se están cargando los catálogos o la docencia. */
    var isLoading by mutableStateOf(false)
        private set

    /** Indica que se está guardando. */
    var isSaving by mutableStateOf(false)
        private set

    /** Error visible en el formulario. */
    var error by mutableStateOf<String?>(null)
        private set

    /** `true` cuando se edita una docencia existente. */
    val isEditMode: Boolean = docenciaId != null

    private var cargado = false
    private var originalDocenteId: Int? = null

    /** Cambia el docente. */
    fun onDocenteIdChange(value: Int?) {
        docenteId = value
        error = null
    }

    /** Cambia el curso; el formulario solo lo permite al crear. */
    fun onCursoIdChange(value: Int?) {
        cursoId = value
        error = null
    }

    /** Cambia la asignatura; el formulario solo lo permite al crear. */
    fun onAsignaturaIdChange(value: Int?) {
        asignaturaId = value
        error = null
    }

    /** Carga catálogos y, si corresponde, la docencia existente. */
    fun cargar() {
        if (cargado) return

        viewModelScope.launch(dispatcher) {
            isLoading = true
            error = null

            val usuariosResult = userRepository.list()
            if (usuariosResult.isFailure) {
                error = mensajeDeError(usuariosResult.exceptionOrNull() as? ApiError)
                isLoading = false
                return@launch
            }

            val cursosResult = courseRepository.list()
            if (cursosResult.isFailure) {
                error = mensajeDeError(cursosResult.exceptionOrNull() as? ApiError)
                isLoading = false
                return@launch
            }

            val asignaturasResult = subjectRepository.list()
            if (asignaturasResult.isFailure) {
                error = mensajeDeError(asignaturasResult.exceptionOrNull() as? ApiError)
                isLoading = false
                return@launch
            }

            docentes = usuariosResult.getOrDefault(emptyList())
                .filter { it.rol == UserRole.DOCENTE }
                .sortedBy { it.nombre.lowercase() }
            cursos = cursosResult.getOrDefault(emptyList())
                .sortedWith(compareBy({ it.anio }, { it.nombre }))
            asignaturas = asignaturasResult.getOrDefault(emptyList())
                .sortedBy { it.nombre }

            if (docenciaId == null) {
                cursoId = initialCourseId?.takeIf { id -> cursos.any { it.id == id } }
            } else {
                val docenciaResult = docenciaRepository.getById(docenciaId)
                if (docenciaResult.isFailure) {
                    error = mensajeDeError(docenciaResult.exceptionOrNull() as? ApiError)
                    isLoading = false
                    return@launch
                }

                val docencia = docenciaResult.getOrThrow()
                docenteId = docencia.docenteId
                cursoId = docencia.cursoId
                asignaturaId = docencia.asignaturaId
                originalDocenteId = docencia.docenteId
            }

            cargado = true
            isLoading = false
        }
    }

    /** Guarda la docencia y avisa cuando termina. */
    fun guardar(onSaved: () -> Unit) {
        val docente = docenteId
        val curso = cursoId
        val asignatura = asignaturaId

        when {
            docente == null -> {
                error = "Selecciona un docente."
                return
            }

            curso == null -> {
                error = "Selecciona un curso."
                return
            }

            asignatura == null -> {
                error = "Selecciona una asignatura."
                return
            }
        }

        if (isEditMode && docente == originalDocenteId) {
            error = "No hay cambios para guardar."
            return
        }

        viewModelScope.launch(dispatcher) {
            isSaving = true
            error = null

            val result = if (docenciaId == null) {
                docenciaRepository.create(
                    CreateDocenciaRequest(
                        docenteId = docente,
                        cursoId = curso,
                        asignaturaId = asignatura
                    )
                )
            } else {
                docenciaRepository.update(
                    id = docenciaId,
                    request = UpdateDocenciaRequest(docenteId = docente)
                )
            }

            isSaving = false

            if (result.isFailure) {
                error = mensajeDeError(result.exceptionOrNull() as? ApiError)
                return@launch
            }

            onSaved()
        }
    }

    /** Traduce el fallo del repositorio a un mensaje visible. */
    private fun mensajeDeError(error: ApiError?): String = when (error) {
        is ApiError.Conflict ->
            "Ya existe una docencia para ese curso y asignatura."
        is ApiError.BadRequest -> error.message
        is ApiError.NotFound -> "El docente, curso o asignatura ya no existe."
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.Unknown -> error.message
        null -> "No se pudo guardar la docencia."
    }

    companion object {

        /** Factoría del formulario con los repositorios y el contexto. */
        fun factory(
            docenciaRepository: DocenciaRepository,
            userRepository: UserRepository,
            courseRepository: CourseRepository,
            subjectRepository: SubjectRepository,
            docenciaId: Int?,
            initialCourseId: Int? = null
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                DocenciaFormViewModel(
                    docenciaRepository = docenciaRepository,
                    userRepository = userRepository,
                    courseRepository = courseRepository,
                    subjectRepository = subjectRepository,
                    docenciaId = docenciaId,
                    initialCourseId = initialCourseId
                )
            }
        }
    }
}
