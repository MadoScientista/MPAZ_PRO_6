package com.ccstudio.mpaz_pro_6.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ccstudio.mpaz_pro_6.data.local.entity.AttemptEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.StudentAnswerEntity

/** Consultas de intentos y respuestas de estudiantes. */
@Dao
interface AttemptDao {

    @Query("SELECT * FROM intento ORDER BY estudiante_id, actividad_id, numero")
    suspend fun getAttempts(): List<AttemptEntity>

    @Query("SELECT * FROM intento WHERE estudiante_id = :studentId ORDER BY actividad_id, numero")
    suspend fun getAttemptsByStudent(studentId: Int): List<AttemptEntity>

    @Query("SELECT * FROM intento WHERE actividad_id = :activityId ORDER BY estudiante_id, numero")
    suspend fun getAttemptsByActivity(activityId: Int): List<AttemptEntity>

    @Query("SELECT * FROM intento WHERE id = :id")
    suspend fun getAttemptById(id: Int): AttemptEntity?

    @Upsert
    suspend fun upsertAttempt(entity: AttemptEntity)

    @Upsert
    suspend fun upsertAttempts(entities: List<AttemptEntity>)

    @Query("DELETE FROM intento")
    suspend fun deleteAllAttempts()

    @Query("SELECT * FROM respuesta_estudiante ORDER BY intento_id, id")
    suspend fun getStudentAnswers(): List<StudentAnswerEntity>

    @Query("SELECT * FROM respuesta_estudiante WHERE intento_id = :attemptId ORDER BY id")
    suspend fun getStudentAnswersByAttempt(attemptId: Int): List<StudentAnswerEntity>

    @Query("SELECT * FROM respuesta_estudiante WHERE id = :id")
    suspend fun getStudentAnswerById(id: Int): StudentAnswerEntity?

    @Upsert
    suspend fun upsertStudentAnswer(entity: StudentAnswerEntity)

    @Upsert
    suspend fun upsertStudentAnswers(entities: List<StudentAnswerEntity>)

    @Query("DELETE FROM respuesta_estudiante")
    suspend fun deleteAllStudentAnswers()
}
