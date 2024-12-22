package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.feature.analysis.data.AnalysisEntity
import innobiz.crm.genompluslab.feature.topic.data.TopicAnalysisEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TopicAnalysisRepository: CoroutineCrudRepository<TopicAnalysisEntity, String> {
    @Query("""
    SELECT DISTINCT a 
    FROM AnalysisEntity a
    JOIN CityAnalysisEntity c ON a.id = c.analysisId
    JOIN TopicAnalysisEntity ta ON a.id = ta.analysisId
    WHERE ta.topicId = :topicId AND c.cityId = :cityId
    ORDER BY a.popularScore DESC
    LIMIT :limit OFFSET :offset
""")
    suspend fun findAnalysesByTopicAndCityWithPagination(
            topicId: String,
            cityId: String,
            limit: Int,
            offset: Int
    ): List<AnalysisEntity>

    @Query("""
    SELECT COUNT(DISTINCT a) 
    FROM AnalysisEntity a
    JOIN CityAnalysisEntity c ON a.id = c.analysisId
    JOIN TopicAnalysisEntity ta ON a.id = ta.analysisId
    WHERE ta.topicId = :topicId AND c.cityId = :cityId
""")
    suspend fun countAnalysesByTopicAndCity(topicId: String, cityId: String): Long
}