package com.ccstudio.mpaz_pro_6.ui.screens.home

import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.mock.MockCourseRepository
import com.ccstudio.mpaz_pro_6.data.mock.MockData
import com.ccstudio.mpaz_pro_6.data.mock.MockStore
import com.ccstudio.mpaz_pro_6.data.mock.MockSubjectRepository
import com.ccstudio.mpaz_pro_6.data.mock.MockUnitRepository
import com.ccstudio.mpaz_pro_6.data.mock.MockUserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas del panel de inicio de la UTP.
 *
 * El panel entrega a la pantalla las listas que alimentan los carruseles: solo
 * las unidades en revisión (los borradores pertenecen al docente) y todas las
 * asignaturas.
 */
class HomeUTPViewModelTest {

    private lateinit var store: MockStore
    private lateinit var viewModel: HomeUTPViewModel

    @Before
    fun setUp() {
        store = MockData.crearStore()
        viewModel = HomeUTPViewModel(
            unitRepository = MockUnitRepository(store),
            courseRepository = MockCourseRepository(store),
            subjectRepository = MockSubjectRepository(store),
            userRepository = MockUserRepository(store),
            dispatcher = Dispatchers.Unconfined
        )
    }

    @Test
    fun `expone solo las unidades en revision`() = runBlocking {
        viewModel.cargar().join()

        val pendientes = viewModel.unidadesPendientes

        assertEquals(1, pendientes.size)
        assertTrue(pendientes.all { it.estado == ContentStatus.IN_REVIEW })
    }

    @Test
    fun `expone las asignaturas y mantiene los contadores`() = runBlocking {
        viewModel.cargar().join()

        assertEquals(3, viewModel.asignaturas.size)
        assertEquals(2, viewModel.cursos.size)
        assertNull(viewModel.error)
        assertFalse(viewModel.isLoading)
    }
}
