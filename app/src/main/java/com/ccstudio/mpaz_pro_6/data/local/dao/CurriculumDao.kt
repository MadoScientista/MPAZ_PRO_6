package com.ccstudio.mpaz_pro_6.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ccstudio.mpaz_pro_6.data.local.entity.ActivityEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AlternativeEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.ContentEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.ContentResourceEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.LearningObjectiveEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.QuestionEntity

/** Consultas del catálogo curricular. */
@Dao
interface CurriculumDao {

    @Query("SELECT * FROM objetivo_aprendizaje ORDER BY unidad_id, codigo")
    suspend fun getObjectives(): List<LearningObjectiveEntity>

    @Query("SELECT * FROM objetivo_aprendizaje WHERE unidad_id = :unitId ORDER BY codigo")
    suspend fun getObjectivesByUnit(unitId: Int): List<LearningObjectiveEntity>

    @Query("SELECT * FROM objetivo_aprendizaje WHERE id = :id")
    suspend fun getObjectiveById(id: Int): LearningObjectiveEntity?

    @Upsert
    suspend fun upsertObjective(entity: LearningObjectiveEntity)

    @Upsert
    suspend fun upsertObjectives(entities: List<LearningObjectiveEntity>)

    @Query("DELETE FROM objetivo_aprendizaje")
    suspend fun deleteAllObjectives()

    @Query("DELETE FROM objetivo_aprendizaje WHERE id = :id")
    suspend fun deleteObjectiveById(id: Int)

    @Query("SELECT * FROM contenido ORDER BY oa_id, orden")
    suspend fun getContents(): List<ContentEntity>

    @Query("SELECT * FROM contenido WHERE oa_id = :oaId ORDER BY orden")
    suspend fun getContentsByObjective(oaId: Int): List<ContentEntity>

    @Query("SELECT * FROM contenido WHERE id = :id")
    suspend fun getContentById(id: Int): ContentEntity?

    @Query(
        """
        SELECT contenido.* FROM contenido
        INNER JOIN objetivo_aprendizaje
            ON objetivo_aprendizaje.id = contenido.oa_id
        WHERE objetivo_aprendizaje.unidad_id = :unitId
        """
    )
    suspend fun getContentsByUnit(unitId: Int): List<ContentEntity>

    @Upsert
    suspend fun upsertContent(entity: ContentEntity)

    @Upsert
    suspend fun upsertContents(entities: List<ContentEntity>)

    @Query("DELETE FROM contenido")
    suspend fun deleteAllContents()

    @Query("DELETE FROM contenido WHERE id = :id")
    suspend fun deleteContentById(id: Int)

    @Query("SELECT * FROM recurso_contenido ORDER BY contenido_id, id")
    suspend fun getContentResources(): List<ContentResourceEntity>

    @Query("SELECT * FROM recurso_contenido WHERE contenido_id = :contentId ORDER BY id")
    suspend fun getContentResourcesByContent(contentId: Int): List<ContentResourceEntity>

    @Query("SELECT * FROM recurso_contenido WHERE id = :id")
    suspend fun getContentResourceById(id: Int): ContentResourceEntity?

    @Query(
        """
        SELECT recurso_contenido.* FROM recurso_contenido
        INNER JOIN contenido ON contenido.id = recurso_contenido.contenido_id
        INNER JOIN objetivo_aprendizaje
            ON objetivo_aprendizaje.id = contenido.oa_id
        WHERE objetivo_aprendizaje.unidad_id = :unitId
        """
    )
    suspend fun getContentResourcesByUnit(unitId: Int): List<ContentResourceEntity>

    @Upsert
    suspend fun upsertContentResource(entity: ContentResourceEntity)

    @Upsert
    suspend fun upsertContentResources(entities: List<ContentResourceEntity>)

    @Query("DELETE FROM recurso_contenido")
    suspend fun deleteAllContentResources()

    @Query("DELETE FROM recurso_contenido WHERE contenido_id = :contentId")
    suspend fun deleteContentResourcesByContent(contentId: Int)

    @Query("DELETE FROM recurso_contenido WHERE id = :id")
    suspend fun deleteContentResourceById(id: Int)

    @Query("SELECT * FROM actividad ORDER BY unidad_id, orden")
    suspend fun getActivities(): List<ActivityEntity>

    @Query("SELECT * FROM actividad WHERE unidad_id = :unitId ORDER BY orden")
    suspend fun getActivitiesByUnit(unitId: Int): List<ActivityEntity>

    @Query(
        """
        SELECT * FROM actividad
        WHERE unidad_id = :unitId AND contenido_id = :contentId
        ORDER BY orden
        """
    )
    suspend fun getActivitiesByUnitAndContent(
        unitId: Int,
        contentId: Int
    ): List<ActivityEntity>

    @Query(
        """
        SELECT * FROM actividad
        WHERE unidad_id = :unitId AND contenido_id IS NULL
        ORDER BY orden
        """
    )
    suspend fun getTransversalActivitiesByUnit(unitId: Int): List<ActivityEntity>

    @Query("SELECT COUNT(*) FROM actividad WHERE contenido_id = :contentId")
    suspend fun countActivitiesByContent(contentId: Int): Int

    @Query("SELECT * FROM actividad WHERE id = :id")
    suspend fun getActivityById(id: Int): ActivityEntity?

    @Upsert
    suspend fun upsertActivity(entity: ActivityEntity)

    @Upsert
    suspend fun upsertActivities(entities: List<ActivityEntity>)

    @Query("DELETE FROM actividad")
    suspend fun deleteAllActivities()

    @Query("DELETE FROM actividad WHERE id = :id")
    suspend fun deleteActivityById(id: Int)

    @Query("SELECT * FROM pregunta ORDER BY actividad_id, orden")
    suspend fun getQuestions(): List<QuestionEntity>

    @Query("SELECT * FROM pregunta WHERE actividad_id = :activityId ORDER BY orden")
    suspend fun getQuestionsByActivity(activityId: Int): List<QuestionEntity>

    @Query("SELECT * FROM pregunta WHERE oa_id = :oaId ORDER BY actividad_id, orden")
    suspend fun getQuestionsByObjective(oaId: Int): List<QuestionEntity>

    @Query("SELECT * FROM pregunta WHERE id = :id")
    suspend fun getQuestionById(id: Int): QuestionEntity?

    @Upsert
    suspend fun upsertQuestion(entity: QuestionEntity)

    @Upsert
    suspend fun upsertQuestions(entities: List<QuestionEntity>)

    @Query("DELETE FROM pregunta")
    suspend fun deleteAllQuestions()

    @Query("DELETE FROM pregunta WHERE id = :id")
    suspend fun deleteQuestionById(id: Int)

    @Query("SELECT * FROM alternativa ORDER BY pregunta_id, id")
    suspend fun getAlternatives(): List<AlternativeEntity>

    @Query("SELECT * FROM alternativa WHERE pregunta_id = :questionId ORDER BY id")
    suspend fun getAlternativesByQuestion(questionId: Int): List<AlternativeEntity>

    @Query("SELECT * FROM alternativa WHERE id = :id")
    suspend fun getAlternativeById(id: Int): AlternativeEntity?

    @Upsert
    suspend fun upsertAlternative(entity: AlternativeEntity)

    @Upsert
    suspend fun upsertAlternatives(entities: List<AlternativeEntity>)

    @Query("DELETE FROM alternativa")
    suspend fun deleteAllAlternatives()

    @Query("DELETE FROM alternativa WHERE id = :id")
    suspend fun deleteAlternativeById(id: Int)

    @Transaction
    suspend fun deleteContentCascade(contentId: Int) {
        deleteContentResourcesByContent(contentId)
        deleteContentById(contentId)
    }
}
