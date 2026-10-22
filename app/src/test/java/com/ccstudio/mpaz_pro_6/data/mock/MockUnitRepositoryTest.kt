package com.ccstudio.mpaz_pro_6.data.mock

import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateLearningUnitRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateLearningUnitRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

/**
 * Pruebas de [MockUnitRepository].
 *
 * Verifican el filtro por asignatura, las validaciones de creación, el cambio de
 * estado con autorizante UTP y fecha, y el borrado en conflicto cuando la unidad
 * tiene asignaciones.
 */
class MockUnitRepositoryTest {

    private lateinit var store: MockStore
    private lateinit var repository: MockUnitRepository

    @Before
    fun setUp() {
        store = MockData.crearStore()
        repository = MockUnitRepository(store)
    }

    @Test
    fun listOrdenaPorAsignaturaYOrden() = runBlocking {
        val units = repository.list().getOrThrow()

        assertEquals(5, units.size)
        assertEquals(listOf(1, 1, 2, 2, 3), units.map { it.asignaturaId })
    }

    @Test
    fun listFiltraPorAsignatura() = runBlocking {
        val units = repository.list(subjectId = 1).getOrThrow()

        assertEquals(2, units.size)
        assertTrue(units.all { it.asignaturaId == 1 })
    }

    @Test
    fun listConAsignaturaInexistenteDevuelveNotFound() = runBlocking {
        val result = repository.list(subjectId = 999)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.NotFound)
    }

    @Test
    fun createConAsignaturaInexistenteDevuelveNotFound() = runBlocking {
        val result = repository.create(
            CreateLearningUnitRequest(
                asignaturaId = 999,
                titulo = "Unidad sin asignatura",
                estado = ContentStatus.DRAFT
            )
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.NotFound)
    }

    @Test
    fun createConTituloVacioDevuelveBadRequest() = runBlocking {
        val result = repository.create(
            CreateLearningUnitRequest(
                asignaturaId = 1,
                titulo = "  ",
                estado = ContentStatus.DRAFT
            )
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.BadRequest)
    }

    @Test
    fun createConEstadoNoInicialDevuelveBadRequest() = runBlocking {
        val result = repository.create(
            CreateLearningUnitRequest(
                asignaturaId = 1,
                titulo = "Unidad publicada",
                estado = ContentStatus.PUBLISHED
            )
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.BadRequest)
    }

    @Test
    fun createGuardaLaUnidadEnBorrador() = runBlocking {
        val unit = repository.create(
            CreateLearningUnitRequest(
                asignaturaId = 1,
                titulo = "Geometría plana",
                estado = ContentStatus.DRAFT
            )
        ).getOrThrow()

        assertEquals(ContentStatus.DRAFT, unit.estado)
        assertNull(unit.autorizadaPorId)
        assertNull(unit.fechaAutorizacion)
    }

    @Test
    fun updateSinCamposDevuelveBadRequest() = runBlocking {
        val result = repository.update(1, UpdateLearningUnitRequest())

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.BadRequest)
    }

    @Test
    fun updateAplicaSoloLosCamposEnviados() = runBlocking {
        val unit = repository.update(1, UpdateLearningUnitRequest(titulo = "Nuevo título")).getOrThrow()

        assertEquals("Nuevo título", unit.titulo)
        assertEquals("Sexto Basico", unit.nivelEducativo)
        assertEquals(ContentStatus.DRAFT, unit.estado)
    }

    @Test
    fun autorizarSinAutorizanteDevuelveBadRequest() = runBlocking {
        val result = repository.update(
            1,
            UpdateLearningUnitRequest(estado = ContentStatus.AUTHORIZED)
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.BadRequest)
    }

    @Test
    fun autorizarSinFechaDevuelveBadRequest() = runBlocking {
        val result = repository.update(
            1,
            UpdateLearningUnitRequest(
                estado = ContentStatus.AUTHORIZED,
                autorizadaPorId = 1
            )
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.BadRequest)
    }

    @Test
    fun autorizarConDocenteDevuelveBadRequest() = runBlocking {
        val result = repository.update(
            1,
            UpdateLearningUnitRequest(
                estado = ContentStatus.AUTHORIZED,
                autorizadaPorId = 2,
                fechaAutorizacion = Instant.parse("2026-05-01T10:00:00Z")
            )
        )

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.BadRequest)
    }

    @Test
    fun autorizarConUptYFechaPasaLaUnidadAAutorizado() = runBlocking {
        val unit = repository.update(
            1,
            UpdateLearningUnitRequest(
                estado = ContentStatus.AUTHORIZED,
                autorizadaPorId = 1,
                fechaAutorizacion = Instant.parse("2026-05-01T10:00:00Z")
            )
        ).getOrThrow()

        assertEquals(ContentStatus.AUTHORIZED, unit.estado)
        assertEquals(1, unit.autorizadaPorId)
        assertNotNull(unit.fechaAutorizacion)
    }

    @Test
    fun deleteDeUnidadConAsignacionesDevuelveConflict() = runBlocking {
        val result = repository.delete(1)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ApiError.Conflict)
    }

    @Test
    fun deleteDeUnidadSinDependenciasEliminaLaUnidad() = runBlocking {
        val result = repository.delete(2)

        assertTrue(result.isSuccess)
        assertEquals(4, repository.list().getOrThrow().size)
    }
}
