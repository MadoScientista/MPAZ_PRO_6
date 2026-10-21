package com.ccstudio.mpaz_pro_6.data.mock

import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateCourseRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateCourseRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas de [MockCourseRepository].
 *
 * Verifican el filtro por año, el rango del año, la unicidad del par nombre-año y
 * el borrado en conflicto cuando el curso tiene estudiantes o asignaciones.
 */
class MockCourseRepositoryTest {

    private lateinit var store: MockStore
    private lateinit var repository: MockCourseRepository

    @Before
    fun setUp() {
        store = MockData.crearStore()
        repository = MockCourseRepository(store)
    }

    @Test
    fun listFiltraPorAnio() = runBlocking {
        val courses = repository.list(anio = 2026).getOrThrow()

        assertEquals(2, courses.size)
        assertTrue(courses.all { it.anio == 2026 })
    }

    @Test
    fun listConAnioFueraDeRangoDevuelveBadRequest() = runBlocking {
        val result = repository.list(anio = 1990)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.BadRequest)
    }

    @Test
    fun createConAnioFueraDeRangoDevuelveBadRequest() = runBlocking {
        val result = repository.create(CreateCourseRequest(nombre = "7A", anio = 2200))

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.BadRequest)
    }

    @Test
    fun createConNombreYAnioDuplicadosDevuelveConflict() = runBlocking {
        val result = repository.create(CreateCourseRequest(nombre = "6A", anio = 2026))

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.Conflict)
    }

    @Test
    fun createConMismoNombreEnOtroAnioEsValido() = runBlocking {
        val course = repository.create(CreateCourseRequest(nombre = "6A", anio = 2027)).getOrThrow()

        assertEquals("6A", course.nombre)
        assertEquals(2027, course.anio)
    }

    @Test
    fun updateSinCamposDevuelveBadRequest() = runBlocking {
        val result = repository.update(1, UpdateCourseRequest())

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.BadRequest)
    }

    @Test
    fun updateAplicaSoloLosCamposEnviados() = runBlocking {
        val course = repository.update(1, UpdateCourseRequest(nombre = "6 A")).getOrThrow()

        assertEquals("6 A", course.nombre)
        assertEquals(2026, course.anio)
    }

    @Test
    fun deleteDeCursoConEstudiantesYAsignacionesDevuelveConflict() = runBlocking {
        val result = repository.delete(1)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.Conflict)
    }
}
