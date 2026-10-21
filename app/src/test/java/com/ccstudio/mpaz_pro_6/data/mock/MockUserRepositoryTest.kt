package com.ccstudio.mpaz_pro_6.data.mock

import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateUserRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateUserRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas de [MockUserRepository].
 *
 * Verifican el inicio de sesión local, la unicidad del `username`, la asignación de
 * curso exclusiva del estudiante, los cursos del docente y el borrado en conflicto.
 */
class MockUserRepositoryTest {

    private lateinit var store: MockStore
    private lateinit var repository: MockUserRepository

    @Before
    fun setUp() {
        store = MockData.crearStore()
        repository = MockUserRepository(store)
    }

    @Test
    fun authenticateConCredencialesCorrectasDevuelveElUsuario() = runBlocking {
        val user = repository.authenticate("elena", "utp123").getOrThrow()

        assertEquals("Elena Soto", user.nombre)
        assertEquals(UserRole.UTP, user.rol)
    }

    @Test
    fun authenticateIgnoraMayusculasEnElUsuario() = runBlocking {
        val user = repository.authenticate("ELENA", "utp123").getOrThrow()

        assertEquals(UserRole.UTP, user.rol)
    }

    @Test
    fun authenticateConContrasenaIncorrectaDevuelveBadRequest() = runBlocking {
        val result = repository.authenticate("elena", "mala")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.BadRequest)
    }

    @Test
    fun authenticateConUsuarioInexistenteDevuelveBadRequest() = runBlocking {
        val result = repository.authenticate("nadie", "clave")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.BadRequest)
    }

    @Test
    fun createConUsernameDuplicadoDevuelveConflict() = runBlocking {
        val result = repository.create(
            CreateUserRequest(
                nombre = "Otra Elena",
                username = "elena",
                password = "clave123",
                rol = UserRole.DOCENTE
            )
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.Conflict)
    }

    @Test
    fun createConUsernameInvalidoDevuelveBadRequest() = runBlocking {
        val result = repository.create(
            CreateUserRequest(
                nombre = "Estudiante",
                username = "a b",
                password = "clave123",
                rol = UserRole.ESTUDIANTE
            )
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.BadRequest)
    }

    @Test
    fun createDeDocenteConCursoDevuelveBadRequest() = runBlocking {
        val result = repository.create(
            CreateUserRequest(
                nombre = "Docente con curso",
                username = "docente.curso",
                password = "clave123",
                rol = UserRole.DOCENTE,
                cursoId = 1
            )
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.BadRequest)
    }

    @Test
    fun createDeEstudianteGuardaElCursoYLaContrasena() = runBlocking {
        val user = repository.create(
            CreateUserRequest(
                nombre = "Estudiante Nuevo",
                username = "nuevo",
                password = "clave123",
                rol = UserRole.ESTUDIANTE,
                cursoId = 2
            )
        ).getOrThrow()

        assertEquals(2, user.cursoId)
        assertEquals(user, repository.authenticate("nuevo", "clave123").getOrThrow())
    }

    @Test
    fun updateDeDocenteAEstudianteAsignaElCurso() = runBlocking {
        val user = repository.update(
            2,
            UpdateUserRequest(rol = UserRole.ESTUDIANTE, cursoId = 1)
        ).getOrThrow()

        assertEquals(UserRole.ESTUDIANTE, user.rol)
        assertEquals(1, user.cursoId)
    }

    @Test
    fun updateAEstudianteSinCursoMantieneElCursoAnterior() = runBlocking {
        val user = repository.update(3, UpdateUserRequest(nombre = "Camila Soto")).getOrThrow()

        assertEquals("Camila Soto", user.nombre)
        assertEquals(1, user.cursoId)
    }

    @Test
    fun deleteDeUsuarioConUnidadesAutorizadasDevuelveConflict() = runBlocking {
        val result = repository.delete(1)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.Conflict)
    }

    @Test
    fun deleteDeUsuarioSinDependenciasEliminaElUsuario() = runBlocking {
        val result = repository.delete(4)

        assertTrue(result.isSuccess)
        assertEquals(3, repository.list().getOrThrow().size)
    }

    @Test
    fun listCoursesDeDocenteDevuelveLosCursosDeSusAsignaciones() = runBlocking {
        val courses = repository.listCourses(2).getOrThrow()

        assertEquals(1, courses.size)
        assertEquals("6A", courses.first().nombre)
    }

    @Test
    fun listCoursesDeEstudianteDevuelveBadRequest() = runBlocking {
        val result = repository.listCourses(3)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.BadRequest)
    }

    @Test
    fun listCoursesDeUsuarioInexistenteDevuelveNotFound() = runBlocking {
        val result = repository.listCourses(999)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.NotFound)
    }

    @Test
    fun updateSinCamposDevuelveBadRequest() = runBlocking {
        val result = repository.update(3, UpdateUserRequest())

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.BadRequest)
    }

    @Test
    fun elUsuarioSinCursoNoTieneCursoAsignado() = runBlocking {
        val user = repository.getById(1).getOrThrow()

        assertNull(user.cursoId)
    }
}
