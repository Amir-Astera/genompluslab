package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.feature.topic.data.TopicEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TopicRepository:  CoroutineCrudRepository<TopicEntity, String>{
    @Query("""
    SELECT DISTINCT t 
    FROM TopicEntity t
    JOIN TopicAnalysisEntity ta ON t.id = ta.topicId
    JOIN CityAnalysisEntity c ON ta.analysisId = c.analysisId
    WHERE c.cityId = :cityId 
    AND LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))
    ORDER BY t.popularScore DESC
    LIMIT :limit OFFSET :offset
""")
    suspend fun findTopicsByNameWithPagination(
            cityId: String,
            name: String,
            limit: Int,
            offset: Int
    ): List<TopicEntity>

    @Query("""
    SELECT COUNT(DISTINCT t.id)
    FROM TopicEntity t
    JOIN TopicAnalysisEntity ta ON t.id = ta.topicId
    JOIN CityAnalysisEntity c ON ta.analysisId = c.analysisId
    WHERE c.cityId = :cityId 
    AND LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))
""")
    suspend fun countTopicsByName(cityId: String, name: String): Long

    @Query("""
    SELECT DISTINCT t 
    FROM TopicEntity t
    JOIN TopicAnalysisEntity ta ON t.id = ta.topicId
    JOIN CityAnalysisEntity c ON ta.analysisId = c.analysisId
    WHERE c.cityId = :cityId 
    AND LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))
    ORDER BY t.popularScore DESC
    LIMIT 1
""")
    suspend fun findFirstTopicByNameInCity(cityId: String, name: String): TopicEntity?
}