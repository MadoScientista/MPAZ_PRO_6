package com.ccstudio.mpaz_pro_6.ui.screens.users

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
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateUserRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateUserRequest
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * Estado del formulario de usuario.
 *
 * Sin `userId` crea; con id carga el usuario y edita. En edición la contraseña
 * vacía no se envía y solo se mandan los campos modificados, porque el `PUT` es
 * parcial.
 *
 * Limitación del contrato: `UpdateUserRequest.cursoId = null` significa "no
 * enviar", así que quitar el curso de un estudiante sin cambiar su rol no se
 * puede expresar. Cambiar el rol a otro distinto de `ESTUDIANTE` sí lo limpia en
 * el backend.
 */
class UserFormViewModel(
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
    private val userId: Int?
) : ViewModel() {

    /** Nombre completo. */
    var nombre by mutableStateOf("")
        private set

    /** Nombre de usuario. */
    var username by mutableStateOf("")
        private set

    /** Contraseña; vacía en edición significa "no cambiar". */
    var password by mutableStateOf("")
        private set

    /** Rol del usuario. */
    var rol by mutableStateOf(UserRole.ESTUDIANTE)
        private set

    /** Curso del estudiante; `null` para los demás roles. */
    var cursoId by mutableStateOf<Int?>(null)
        private set

    /** Cursos disponibles para el selector. */
    var cursos by mutableStateOf<List<Course>>(emptyList())
        private set

    /** Indica si se está cargando el usuario o los cursos. */
    var isLoading by mutableStateOf(false)
        private set

    /** Indica que se está guardando. */
    var isSaving by mutableStateOf(false)
        private set

    /** Mensaje de error visible en el formulario. */
    var error by mutableStateOf<String?>(null)
        private set

    /** `true` cuando se edita un usuario existente. */
    val isEditMode: Boolean = userId != null

    private var cargado = false
    private var originalNombre = ""
    private var originalUsername = ""
    private var originalRol = UserRole.ESTUDIANTE
    private var originalCursoId: Int? = null

    /** Cambia el nombre. */
    fun onNombreChange(value: String) {
        nombre = value
        error = null
    }

    /** Cambia el nombre de usuario. */
    fun onUsernameChange(value: String) {
        username = value
        error = null
    }

    /** Cambia la contraseña. */
    fun onPasswordChange(value: String) {
        password = value
        error = null
    }

    /** Cambia el rol y limpia el curso si deja de ser estudiante. */
    fun onRolChange(value: UserRole) {
        rol = value
        if (value != UserRole.ESTUDIANTE) {
            cursoId = null
        }
        error = null
    }

    /** Cambia el curso del estudiante. */
    fun onCursoIdChange(value: Int?) {
        cursoId = value
        error = null
    }

    /** Carga los cursos y, en edición, el usuario. */
    fun cargar() {
        if (cargado) return

        viewModelScope.launch {
            isLoading = true
            error = null

            // Los cursos son un dato de apoyo: si fallan, el formulario sigue.
            cursos = courseRepository.list().getOrDefault(emptyList())

            if (userId != null) {
                val result = userRepository.getById(userId)

                if (result.isFailure) {
                    error = mensajeDeError(result.exceptionOrNull() as? ApiError)
                    isLoading = false
                    return@launch
                }

                val user = result.getOrNull()

                if (user == null) {
                    error = "El usuario ya no existe."
                    isLoading = false
                    return@launch
                }

                nombre = user.nombre
                username = user.username
                rol = user.rol
                cursoId = user.cursoId
                originalNombre = user.nombre
                originalUsername = user.username
                originalRol = user.rol
                originalCursoId = user.cursoId
            }

            cargado = true
            isLoading = false
        }
    }

    /** Guarda el usuario y avisa a la pantalla cuando termina. */
    fun guardar(onSaved: () -> Unit) {
        val nombreLimpio = nombre.trim()
        val usernameLimpio = username.trim()
        val cursoFinal = if (rol == UserRole.ESTUDIANTE) cursoId else null

        when {
            nombreLimpio.isBlank() -> {
                error = "El nombre es obligatorio."
                return
            }

            !USERNAME_PATTERN.matches(usernameLimpio) -> {
                error = "El usuario debe tener entre 3 y 100 caracteres: letras, números, punto, guion o guion bajo."
                return
            }

            !isEditMode && password.isBlank() -> {
                error = "La contraseña es obligatoria para crear el usuario."
                return
            }
        }

        viewModelScope.launch {
            isSaving = true
            error = null

            val result = if (userId == null) {
                userRepository.create(
                    CreateUserRequest(
                        nombre = nombreLimpio,
                        username = usernameLimpio,
                        password = password,
                        rol = rol,
                        cursoId = cursoFinal
                    )
                )
            } else {
                val nombreCambiado = nombreLimpio.takeIf { it != originalNombre }
                val usernameCambiado = usernameLimpio.takeIf { it != originalUsername }
                val passwordCambiada = password.takeIf { it.isNotBlank() }
                val rolCambiado = rol.takeIf { it != originalRol }
                val cursoCambiado = cursoFinal.takeIf { it != originalCursoId }

                if (nombreCambiado == null &&
                    usernameCambiado == null &&
                    passwordCambiada == null &&
                    rolCambiado == null &&
                    cursoCambiado == null
                ) {
                    error = "No hay cambios para guardar."
                    isSaving = false
                    return@launch
                }

                userRepository.update(
                    userId,
                    UpdateUserRequest(
                        nombre = nombreCambiado,
                        username = usernameCambiado,
                        password = passwordCambiada,
                        rol = rolCambiado,
                        cursoId = cursoCambiado
                    )
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

    /** Traduce el fallo del repositorio a un mensaje para el formulario. */
    private fun mensajeDeError(error: ApiError?): String = when (error) {
        is ApiError.Conflict -> "Ya existe un usuario con ese nombre de usuario."
        is ApiError.BadRequest -> error.message
        is ApiError.NotFound -> "El usuario ya no existe."
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.Unknown -> error.message
        null -> "No se pudo guardar el usuario."
    }

    companion object {

        /** Regla del contrato para el `username`: 3 a 100 caracteres permitidos. */
        private val USERNAME_PATTERN = Regex("^[A-Za-z0-9._-]{3,100}$")

        /** Factoría del formulario con los repositorios y el id opcional. */
        fun factory(
            userRepository: UserRepository,
            courseRepository: CourseRepository,
            userId: Int?
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UserFormViewModel(
                    userRepository = userRepository,
                    courseRepository = courseRepository,
                    userId = userId
                )
            }
        }
    }
}
