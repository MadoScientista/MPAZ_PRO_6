package com.ccstudio.mpaz_pro_6.ui.navigation

import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas de [bottomDestinationsFor].
 *
 * Fijan los destinos de la barra inferior por rol y evitan que un cambio de
 * navegación deje tabs duplicados o rutas vacías.
 */
class RoleBottomDestinationsTest {

    @Test
    fun soloElDocenteTieneBarraEnElPiloto() {
        assertEquals(
            listOf(
                AppRoutes.HOME,
                AppRoutes.UNITS,
                AppRoutes.ASSIGNMENTS,
                AppRoutes.REPORTS
            ),
            bottomDestinationsFor(UserRole.DOCENTE).map { it.route }
        )
        assertTrue(bottomDestinationsFor(UserRole.UTP).isEmpty())
        assertTrue(bottomDestinationsFor(UserRole.ESTUDIANTE).isEmpty())
    }

    @Test
    fun ningunRolRepiteDestinosNiDejaRutasVacias() {
        UserRole.entries.forEach { role ->
            val destinations = bottomDestinationsFor(role)

            assertTrue(
                "El rol $role repite rutas",
                destinations.map { it.route }.distinct().size == destinations.size
            )
            assertTrue(
                "El rol $role tiene una ruta vacía",
                destinations.all { it.route.isNotBlank() }
            )
            assertTrue(
                "El rol $role tiene una etiqueta vacía",
                destinations.all { it.label.isNotBlank() }
            )
        }
    }
}
