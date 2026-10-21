package com.ccstudio.mpaz_pro_6.data.mock

import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateSubjectRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateSubjectRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas de [MockSubjectRepository].
 *
 * Verifican las reglas del backend: nombre obligatorio, longitud máxima, unicidad
 * del nombre y borrado en conflicto cuando la asignatura tiene unidades.
 */
class MockSubjectRepositoryTest {

    private lateinit var store: MockStore
    private lateinit var repository: MockSubjectRepository

    @Before
    fun setUp() {
        store = MockData.crearStore()
        repository = MockSubjectRepository(store)
    }

    @Test
    fun listDevuelveLasAsignaturasSembradas() = runBlocking {
        val subjects = repository.list().getOrThrow()

        assertEquals(3, subjects.size)
        assertEquals("Ciencias Naturales", subjects.first().nombre)
    }

    @Test
    fun getByIdDeAsignaturaInexistenteDevuelveNotFound() = runBlocking {
        val result = repository.getById(999)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.NotFound)
    }

    @Test
    fun createConNombreDuplicadoDevuelveConflict() = runBlocking {
        val result = repository.create(CreateSubjectRequest(nombre = "Matemática"))

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.Conflict)
    }

    @Test
    fun createConNombreVacioDevuelveBadRequest() = runBlocking {
        val result = repository.create(CreateSubjectRequest(nombre = "   "))

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.BadRequest)
    }

    @Test
    fun createGuardaLaAsignaturaConIdNuevo() = runBlocking {
        val subject = repository.create(CreateSubjectRequest(nombre = "Historia")).getOrThrow()

        assertEquals(4, subject.id)
        assertEquals("Historia", subject.nombre)
    }

    @Test
    fun updateRechazaElNombreDeOtraAsignatura() = runBlocking {
        val result = repository.update(1, UpdateSubjectRequest(nombre = "Lenguaje"))

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.Conflict)
    }

    @Test
    fun updateAplicaElNombreEnviado() = runBlocking {
        val subject = repository.update(1, UpdateSubjectRequest(nombre = "Matemáticas")).getOrThrow()

        assertEquals("Matemáticas", subject.nombre)
    }

    @Test
    fun deleteDeAsignaturaConUnidadesDevuelveConflict() = runBlocking {
        val result = repository.delete(1)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.Conflict)
    }

    @Test
    fun deleteDeAsignaturaSinUnidadesEliminaLaAsignatura() = runBlocking {
        // Las tres asignaturas sembradas tienen unidades: se crea una sin ellas.
        val subject = repository.create(CreateSubjectRequest(nombre = "Historia")).getOrThrow()

        val result = repository.delete(subject.id)

        assertTrue(result.isSuccess)
        assertEquals(3, repository.list().getOrThrow().size)
    }
}
