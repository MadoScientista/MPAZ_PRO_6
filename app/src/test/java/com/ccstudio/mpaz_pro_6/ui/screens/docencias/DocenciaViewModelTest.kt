package com.ccstudio.mpaz_pro_6.ui.screens.docencias

import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.mock.MockCourseRepository
import com.ccstudio.mpaz_pro_6.data.mock.MockData
import com.ccstudio.mpaz_pro_6.data.mock.MockSubjectRepository
import com.ccstudio.mpaz_pro_6.data.mock.MockUserRepository
import com.ccstudio.mpaz_pro_6.data.model.Docencia
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateDocenciaRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateDocenciaRequest
import com.ccstudio.mpaz_pro_6.data.repository.DocenciaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Pruebas de las pantallas de docencias de la UTP. */
class DocenciaViewModelTest {

    private lateinit var docenciaRepository: FakeDocenciaRepository

    @Before
    fun setUp() {
        docenciaRepository = FakeDocenciaRepository(
            listOf(
                docencia(id = 1, docenteId = 2, cursoId = 1, asignaturaId = 1),
                docencia(id = 2, docenteId = 2, cursoId = 2, asignaturaId = 2)
            )
        )
    }

    @Test
    fun `el listado carga docencias y catálogos y aplica el curso inicial`() = runBlocking {
        val store = MockData.crearStore()
        val viewModel = DocenciaListViewModel(
            docenciaRepository = docenciaRepository,
            userRepository = MockUserRepository(store),
            courseRepository = MockCourseRepository(store),
            subjectRepository = MockSubjectRepository(store),
            initialCourseId = 1,
            dispatcher = Dispatchers.Unconfined
        )

        viewModel.cargar()

        assertNull(viewModel.error)
        assertEquals(2, viewModel.docencias.size)
        assertEquals(1, viewModel.docentes.size)
        assertEquals(1, viewModel.selectedCourseId)
    }

    @Test
    fun `crear una docencia conserva el curso inicial y guarda los tres ids`() = runBlocking {
        val store = MockData.crearStore()
        var saved = false
        val viewModel = DocenciaFormViewModel(
            docenciaRepository = docenciaRepository,
            userRepository = MockUserRepository(store),
            courseRepository = MockCourseRepository(store),
            subjectRepository = MockSubjectRepository(store),
            docenciaId = null,
            initialCourseId = 2,
            dispatcher = Dispatchers.Unconfined
        )

        viewModel.cargar()
        assertEquals(2, viewModel.cursoId)

        viewModel.onDocenteIdChange(2)
        viewModel.onAsignaturaIdChange(1)
        viewModel.guardar { saved = true }

        assertTrue(saved)
        assertEquals(2, docenciaRepository.lastCreated?.cursoId)
        assertEquals(2, docenciaRepository.lastCreated?.docenteId)
        assertEquals(1, docenciaRepository.lastCreated?.asignaturaId)
    }

    @Test
    fun `editar sin cambiar el docente se rechaza antes de llamar al repositorio`() = runBlocking {
        val store = MockData.crearStore()
        val viewModel = DocenciaFormViewModel(
            docenciaRepository = docenciaRepository,
            userRepository = MockUserRepository(store),
            courseRepository = MockCourseRepository(store),
            subjectRepository = MockSubjectRepository(store),
            docenciaId = 1,
            dispatcher = Dispatchers.Unconfined
        )

        viewModel.cargar()
        viewModel.guardar { error("No debería guardarse") }

        assertEquals(
            "No hay cambios para guardar.",
            viewModel.error
        )
    }

    @Test
    fun `eliminar una docencia con asignaciones muestra el conflicto`() = runBlocking {
        val store = MockData.crearStore()
        docenciaRepository.conflictOnDelete = true
        val viewModel = DocenciaListViewModel(
            docenciaRepository = docenciaRepository,
            userRepository = MockUserRepository(store),
            courseRepository = MockCourseRepository(store),
            subjectRepository = MockSubjectRepository(store),
            dispatcher = Dispatchers.Unconfined
        )

        viewModel.cargar()
        viewModel.eliminar(1)

        assertTrue(viewModel.mensajeOperacion?.contains("asignaciones") == true)
    }

    private fun docencia(
        id: Int,
        docenteId: Int,
        cursoId: Int,
        asignaturaId: Int
    ) = Docencia(
        id = id,
        docenteId = docenteId,
        docenteNombre = "Docente",
        cursoId = cursoId,
        cursoNombre = if (cursoId == 1) "6A" else "6B",
        asignaturaId = asignaturaId,
        asignaturaNombre = if (asignaturaId == 1) "Matemática" else "Ciencias Naturales"
    )
}

/** Repositorio de docencias en memoria para las pruebas. */
private class FakeDocenciaRepository(
    initial: List<Docencia>
) : DocenciaRepository {

    private val docencias = initial.toMutableList()

    var lastCreated: CreateDocenciaRequest? = null
        private set
    var conflictOnDelete: Boolean = false

    override suspend fun list(docenteId: Int?): Result<List<Docencia>> =
        Result.success(docencias.filter { docenteId == null || it.docenteId == docenteId })

    override suspend fun getById(id: Int): Result<Docencia> =
        docencias.firstOrNull { it.id == id }
            ?.let { Result.success(it) }
            ?: Result.failure(ApiError.NotFound("Docencia no encontrada."))

    override suspend fun create(request: CreateDocenciaRequest): Result<Docencia> {
        lastCreated = request
        val newDocencia = Docencia(
            id = (docencias.maxOfOrNull { it.id } ?: 0) + 1,
            docenteId = request.docenteId,
            docenteNombre = "Docente",
            cursoId = request.cursoId,
            cursoNombre = "Curso",
            asignaturaId = request.asignaturaId,
            asignaturaNombre = "Asignatura"
        )
        docencias.add(newDocencia)
        return Result.success(newDocencia)
    }

    override suspend fun update(id: Int, request: UpdateDocenciaRequest): Result<Docencia> {
        val current = docencias.firstOrNull { it.id == id }
            ?: return Result.failure(ApiError.NotFound("Docencia no encontrada."))
        val updated = current.copy(
            docenteId = request.docenteId,
            docenteNombre = "Docente actualizado"
        )
        docencias[docencias.indexOf(current)] = updated
        return Result.success(updated)
    }

    override suspend fun delete(id: Int): Result<Unit> {
        if (conflictOnDelete) {
            return Result.failure(ApiError.Conflict("Tiene asignaciones asociadas."))
        }
        docencias.removeAll { it.id == id }
        return Result.success(Unit)
    }
}
