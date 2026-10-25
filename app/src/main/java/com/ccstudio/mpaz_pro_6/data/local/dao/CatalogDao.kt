package com.ccstudio.mpaz_pro_6.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ccstudio.mpaz_pro_6.data.local.entity.CourseEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.SubjectEntity

/** Consultas de asignaturas y cursos. */
@Dao
interface CatalogDao {

    @Query("SELECT * FROM asignatura ORDER BY nombre")
    suspend fun getSubjects(): List<SubjectEntity>

    @Query("SELECT * FROM asignatura WHERE id = :id")
    suspend fun getSubjectById(id: Int): SubjectEntity?

    @Upsert
    suspend fun upsertSubject(entity: SubjectEntity)

    @Upsert
    suspend fun upsertSubjects(entities: List<SubjectEntity>)

    @Query("DELETE FROM asignatura")
    suspend fun deleteAllSubjects()

    @Query("DELETE FROM asignatura WHERE id = :id")
    suspend fun deleteSubjectById(id: Int)

    @Query("SELECT * FROM curso ORDER BY anio, nombre")
    suspend fun getCourses(): List<CourseEntity>

    @Query("SELECT * FROM curso WHERE anio = :anio ORDER BY nombre")
    suspend fun getCoursesByYear(anio: Int): List<CourseEntity>

    @Query("SELECT * FROM curso WHERE id = :id")
    suspend fun getCourseById(id: Int): CourseEntity?

    @Upsert
    suspend fun upsertCourse(entity: CourseEntity)

    @Upsert
    suspend fun upsertCourses(entities: List<CourseEntity>)

    @Query("DELETE FROM curso")
    suspend fun deleteAllCourses()

    @Query("DELETE FROM curso WHERE id = :id")
    suspend fun deleteCourseById(id: Int)
}
