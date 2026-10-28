package com.ccstudio.mpaz_pro_6.data.local.repository

import androidx.room.withTransaction
import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.enums.ObjectiveLevel
import com.ccstudio.mpaz_pro_6.data.enums.ReportStatus
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.local.MpazDatabase
import com.ccstudio.mpaz_pro_6.data.local.dao.AssignmentDao
import com.ccstudio.mpaz_pro_6.data.local.dao.AttemptDao
import com.ccstudio.mpaz_pro_6.data.local.dao.CurriculumDao
import com.ccstudio.mpaz_pro_6.data.local.dao.ReportDao
import com.ccstudio.mpaz_pro_6.data.local.dao.UnitDao
import com.ccstudio.mpaz_pro_6.data.local.dao.UserDao
import com.ccstudio.mpaz_pro_6.data.local.entity.PedagogicalReportEntity
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.model.PedagogicalReport
import com.ccstudio.mpaz_pro_6.data.model.ReportObjectiveDetail
import com.ccstudio.mpaz_pro_6.data.model.requests.CreatePedagogicalReportRequest
import com.ccstudio.mpaz_pro_6.data.repository.PedagogicalReportRepository
import java.time.Instant

/** Informes pedagógicos locales, con cálculo del detalle por OA. */
class LocalPedagogicalReportRepository(
    private val database: MpazDatabase,
    private val dao: ReportDao,
    private val unitDao: UnitDao,
    private val userDao: UserDao,
    private val curriculumDao: CurriculumDao,
    private val attemptDao: AttemptDao,
    private val assignmentDao: AssignmentDao
) : PedagogicalReportRepository {

    override suspend fun list(
        studentId: Int?,
        unitId: Int?
    ): Result<List<PedagogicalReport>> = localCall {
        dao.getReports()
            .filter { studentId == null || it.estudianteId == studentId }
            .filter { unitId == null || it.unidadId == unitId }
            .map { it.toDomain() }
    }

    override suspend fun getById(id: Int): Result<PedagogicalReport> = localCall {
        dao.getReportById(id).orNotFound("Informe").getOrThrow().toDomain()
    }

    override suspend fun generate(
        request: CreatePedagogicalReportRequest
    ): Result<PedagogicalReport> = localCall {
        userDao.getUserById(request.estudianteId).orNotFound("Estudiante").getOrThrow()
        unitDao.getUnitById(request.unidadId).orNotFound("Unidad").getOrThrow()
        userDao.getUserById(request.docenteId).orNotFound("Docente").getOrThrow()
        val existing = dao.getReports()
        if (existing.any {
                it.estudianteId == request.estudianteId && it.unidadId == request.unidadId
            }
        ) {
            throw ApiError.Conflict("Ya existe un informe para ese estudiante y unidad.")
        }

        database.withTransaction {
            val report = PedagogicalReportEntity(
                id = existing.nextId { it.id },
                estudianteId = request.estudianteId,
                unidadId = request.unidadId,
                docenteId = request.docenteId,
                fechaGeneracion = Instant.now(),
                estado = ReportStatus.PENDING_VALIDATION,
                fechaValidacion = null
            )
            dao.upsertReport(report)
            dao.upsertReportDetails(calcularDetalles(report).map { it.toEntity(report.id) })
            report.toDomain()
        }
    }

    override suspend fun validate(id: Int): Result<PedagogicalReport> = localCall {
        val current = dao.getReportById(id).orNotFound("Informe").getOrThrow()
        if (current.estado == ReportStatus.VALIDATED) {
            throw ApiError.Conflict("El informe ya fue validado.")
        }
        val updated = current.copy(
            estado = ReportStatus.VALIDATED,
            fechaValidacion = Instant.now()
        )
        dao.upsertReport(updated)
        updated.toDomain()
    }

    override suspend fun listObjectiveDetails(
        reportId: Int
    ): Result<List<ReportObjectiveDetail>> = localCall {
        dao.getReportById(reportId).orNotFound("Informe").getOrThrow()
        dao.getReportDetailsByReport(reportId).map { it.toDomain() }
    }

    private suspend fun calcularDetalles(
        report: PedagogicalReportEntity
    ): List<ReportObjectiveDetail> {
        val student = userDao.getUserById(report.estudianteId)
        val courseId = student?.cursoId ?: return emptyList()
        val assignment = assignmentDao.getAssignments().firstOrNull {
            it.cursoId == courseId && it.unidadId == report.unidadId
        } ?: return emptyList()
        val assignedActivityIds = assignmentDao
            .getAssignmentActivitiesByAssignment(assignment.id)
            .map { it.actividadId }
            .toSet()
        val activityIds = curriculumDao.getActivitiesByUnit(report.unidadId)
            .filter {
                it.id in assignedActivityIds &&
                    it.tipo != ActivityType.FINAL_TEST
            }
            .map { it.id }
            .toSet()
        val attempts = attemptDao.getAttemptsByStudent(report.estudianteId)
            .filter { it.fechaFin != null && it.actividadId in activityIds }
            .groupBy { it.actividadId }
            .mapNotNull { (_, values) -> values.maxByOrNull { it.fechaFin ?: Instant.MIN } }

        val totals = mutableMapOf<Int, Int>()
        val corrects = mutableMapOf<Int, Int>()
        attempts.forEach { attempt ->
            attemptDao.getStudentAnswersByAttempt(attempt.id).forEach answerLoop@ { answer ->
                val question = curriculumDao.getQuestionById(answer.preguntaId)
                    ?: return@answerLoop
                if (question.unidadId != report.unidadId) return@answerLoop
                totals[question.oaId] = (totals[question.oaId] ?: 0) + 1
                if (answer.esCorrecta) {
                    corrects[question.oaId] = (corrects[question.oaId] ?: 0) + 1
                }
            }
        }

        return totals.map { (oaId, total) ->
            val correct = corrects[oaId] ?: 0
            val level = if (correct * 100.0 / total >= 70.0) {
                ObjectiveLevel.STRENGTH
            } else {
                ObjectiveLevel.SUPPORT
            }
            ReportObjectiveDetail(oaId = oaId, total = total, correctas = correct, nivel = level)
        }.sortedBy { it.oaId }
    }
}
