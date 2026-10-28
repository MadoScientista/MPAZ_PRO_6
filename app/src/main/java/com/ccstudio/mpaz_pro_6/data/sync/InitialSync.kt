package com.ccstudio.mpaz_pro_6.data.sync

import androidx.room.withTransaction
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.error.apiCall
import com.ccstudio.mpaz_pro_6.data.local.MpazDatabase
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.data.remote.MpazApi
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDomain

/**
 * Sincronización inicial del catálogo y de los datos base del usuario.
 *
 * Descarga todo antes de tocar Room: si una llamada falla, la caché anterior
 * queda intacta. La escritura se hace en una sola transacción y en el orden de
 * dependencias de las claves foráneas.
 *
 * Las asociaciones, intentos, respuestas e informes no forman parte del catálogo
 * inicial; se cargan de forma lazy. Sus filas locales se descartan para poder
 * reemplazar las entidades de las que dependen sin violar FK.
 */
class InitialSync(
    private val api: MpazApi,
    private val database: MpazDatabase
) {

    /**
     * Descarga y reemplaza el catálogo local.
     *
     * `/api/usuarios` solo está disponible para la UTP. Para los demás roles se
     * cachea únicamente el usuario de la sesión. Las docencias se descargan solo
     * para UTP y docentes; los estudiantes no pueden consultarlas.
     *
     * @param currentUser usuario autenticado que se conserva en la caché cuando
     * el rol no puede listar todos los usuarios.
     */
    suspend fun sync(currentUser: User? = null): Result<Unit> = apiCall {
        val role = currentUser?.rol
        val courses = api.getCourses().map { it.toDomain() }
        val users = if (role == UserRole.UTP) {
            api.getUsers().map { it.toDomain() }
        } else {
            listOfNotNull(currentUser)
        }
        val subjects = api.getSubjects().map { it.toDomain() }
        val docencias = if (role == UserRole.UTP || role == UserRole.DOCENTE) {
            api.getDocencias().map { it.toDomain() }
        } else {
            emptyList()
        }
        val units = api.getUnits().map { it.toDomain() }
        val objectives = api.getObjectives().map { it.toDomain() }
        val contents = api.getContents().map { it.toDomain() }
        val resources = api.getContentResources().map { it.toDomain() }
        val activities = api.getActivities().map { it.toDomain() }
        val questions = api.getQuestions().map { it.toDomain() }
        val alternatives = api.getAlternatives().map { it.toDomain() }
        val assignments = api.getAssignments().map { it.toDomain() }

        database.withTransaction {
            val catalog = database.catalogDao()
            val usersDao = database.userDao()
            val docenciaDao = database.docenciaDao()
            val unitsDao = database.unitDao()
            val curriculum = database.curriculumDao()
            val assignmentDao = database.assignmentDao()
            val attemptDao = database.attemptDao()
            val reportDao = database.reportDao()

            reportDao.deleteAllReportDetails()
            reportDao.deleteAllReports()
            attemptDao.deleteAllStudentAnswers()
            attemptDao.deleteAllAttempts()
            assignmentDao.deleteAllAssignmentActivities()
            assignmentDao.deleteAllAssignmentObjectives()
            assignmentDao.deleteAllAssignments()
            curriculum.deleteAllAlternatives()
            curriculum.deleteAllQuestions()
            curriculum.deleteAllActivities()
            curriculum.deleteAllContentResources()
            curriculum.deleteAllContents()
            curriculum.deleteAllObjectives()
            unitsDao.deleteAllUnits()
            docenciaDao.deleteAllDocencias()
            catalog.deleteAllSubjects()
            usersDao.deleteAllUsers()
            catalog.deleteAllCourses()

            catalog.upsertCourses(courses.map { it.toEntity() })
            usersDao.upsertUsers(users.map { it.toEntity() })
            catalog.upsertSubjects(subjects.map { it.toEntity() })
            docenciaDao.upsertDocencias(docencias.map { it.toEntity() })
            unitsDao.upsertUnits(units.map { it.toEntity() })
            curriculum.upsertObjectives(objectives.map { it.toEntity() })
            curriculum.upsertContents(contents.map { it.toEntity() })
            curriculum.upsertContentResources(resources.map { it.toEntity() })
            curriculum.upsertActivities(activities.map { it.toEntity() })
            curriculum.upsertQuestions(questions.map { it.toEntity() })
            curriculum.upsertAlternatives(alternatives.map { it.toEntity() })
            assignmentDao.upsertAssignments(assignments.map { it.toEntity() })
        }
    }
}
