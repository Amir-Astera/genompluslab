package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.feature.analytics.domain.models.TopicWithPopularity
import innobiz.crm.genompluslab.feature.topic.data.TopicAnalysisEntity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AnalyticsTopicRepository: CoroutineCrudRepository<TopicAnalysisEntity, String> {
    @Query("""
    SELECT t.id AS topic_id,
           SUM(s.salesCount * :salesWeight +
               s.viewsCount * :viewsWeight +
               s.cartCount * :cartAddsWeight) *
               CASE
                   WHEN s.lastSaleDate = CURRENT_DATE THEN 1.0
                   WHEN s.lastSaleDate >= CURRENT_DATE - INTERVAL '7 days' THEN 0.8
                   WHEN s.lastSaleDate >= CURRENT_DATE - INTERVAL '30 days' THEN 0.5
                   ELSE 0.2
               END AS topic_popularity
    FROM TopicAnalysisEntity ta
    JOIN AnalyticsAnalysisEntity s ON ta.analysisId = s.analysisId
    JOIN CityAnalysisEntity ca ON s.analysisId = ca.analysisId
    JOIN TopicEntity t ON ta.topicId = t.id
    WHERE ca.cityId = :cityId
    GROUP BY t.id
    ORDER BY topic_popularity DESC
    LIMIT :limit
""")
    fun findTopPopularTopicsByCity(
            cityId: String,
            salesWeight: Double,
            viewsWeight: Double,
            cartAddsWeight: Double,
            limit: Int
    ): List<TopicWithPopularity>
}