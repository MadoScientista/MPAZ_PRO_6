package com.ccstudio.mpaz_pro_6.data.repository

import com.ccstudio.mpaz_pro_6.data.model.PedagogicalReport
import com.ccstudio.mpaz_pro_6.data.model.ReportObjectiveDetail
import com.ccstudio.mpaz_pro_6.data.model.requests.CreatePedagogicalReportRequest

/**
 * Repositorio de informes pedagógicos (`/api/informes-pedagogicos`).
 *
 * Todas las operaciones son `suspend` y devuelven `Result`; el fallo es siempre
 * un `ApiError`.
 */
interface PedagogicalReportRepository {

    /**
     * Lista los informes ordenados por id.
     *
     * @param studentId filtro opcional del backend (`?estudianteId`).
     * @param unitId filtro opcional del backend (`?unidadId`).
     * Ambos filtros son combinables.
     */
    suspend fun list(
        studentId: Int? = null,
        unitId: Int? = null
    ): Result<List<PedagogicalReport>>

    /** Obtiene un informe por id. */
    suspend fun getById(id: Int): Result<PedagogicalReport>

    /**
     * Genera el informe consolidado de un estudiante en una unidad.
     *
     * El backend calcula la fecha de generación, el detalle por objetivo de
     * aprendizaje y el nivel de cada objetivo (umbral del 70 %).
     */
    suspend fun generate(request: CreatePedagogicalReportRequest): Result<PedagogicalReport>

    /** Valida el informe: fija el estado `VALIDADO` y la fecha de validación. */
    suspend fun validate(id: Int): Result<PedagogicalReport>

    /** Lista el detalle por objetivo de aprendizaje del informe. */
    suspend fun listObjectiveDetails(reportId: Int): Result<List<ReportObjectiveDetail>>
}
