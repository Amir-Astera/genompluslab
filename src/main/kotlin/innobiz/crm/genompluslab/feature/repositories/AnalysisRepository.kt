package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.feature.analysis.data.AnalysisEntity
import innobiz.crm.genompluslab.feature.analytics.domain.models.AnalysisWithPopularity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AnalysisRepository: CoroutineCrudRepository<AnalysisEntity, String> {
    @Query("""
    SELECT a 
    FROM AnalysisEntity a
    JOIN CityAnalysisEntity c ON a.id = c.analysisId
    LEFT JOIN AnalyticsAnalysisEntity s ON s.analysisId = a.id
    WHERE c.cityId = :cityId
    AND LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))
    ORDER BY 
        CASE 
            WHEN s.salesCount IS NOT NULL THEN 
                (s.salesCount * :salesWeight + s.viewsCount * :viewsWeight + s.cartCount * :cartAddsWeight) * 
                CASE 
                    WHEN s.lastSaleDate = CURRENT_DATE THEN 1.0
                    WHEN s.lastSaleDate >= CURRENT_DATE - INTERVAL '7 days' THEN 0.8
                    WHEN s.lastSaleDate >= CURRENT_DATE - INTERVAL '30 days' THEN 0.5
                    ELSE 0.2
                END
            ELSE 0
        END DESC, a.name ASC
    LIMIT :limit OFFSET :offset
""")
    suspend fun findAnalysesByNameWithPopularityAndPagination(
            cityId: String,
            name: String,
            salesWeight: Double,
            viewsWeight: Double,
            cartAddsWeight: Double,
            limit: Int,
            offset: Int
    ): List<AnalysisEntity>

    @Query("""
    SELECT COUNT(a) 
    FROM AnalysisEntity a
    JOIN CityAnalysisEntity c ON a.id = c.analysisId
    LEFT JOIN AnalyticsAnalysisEntity s ON s.analysisId = a.id
    WHERE c.cityId = :cityId
    AND LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))
""")
    suspend fun countAnalysesByNameWithPopularity(
            cityId: String,
            name: String,
    ): Long

    //Не используется, заменен DatabaseClient
//    @Query("""
//    SELECT a.id AS analysis_id,
//           SUM(s.sales_count * :salesWeight +
//               s.views_count * :viewsWeight +
//               s.cart_count * :cartAddsWeight) *
//               CASE
//                   WHEN s.last_sale_date = CURRENT_DATE THEN 1.0
//                   WHEN s.last_sale_date >= CURRENT_DATE - INTERVAL '7 days' THEN 0.8
//                   WHEN s.last_sale_date >= CURRENT_DATE - INTERVAL '30 days' THEN 0.5
//                   ELSE 0.2
//               END AS popular_score
//    FROM analytics_popular_analysis s
//    JOIN topic_analysis ta ON s.analysis_id = ta.analysis_id
//    JOIN city_analysis ca ON s.analysis_id = ca.analysis_id
//    WHERE ta.topic_id = :topicId
//      AND ca.city_id = :cityId
//    GROUP BY s.analysis_id
//    ORDER BY popular_score DESC
//    LIMIT :limit OFFSET :offset
//""")
//    fun calculatePopularAnalysesByTopicAndCity(
//            topicId: String,
//            cityId: String,
//            salesWeight: Double,
//            viewsWeight: Double,
//            cartAddsWeight: Double,
//            limit: Int,
//            offset: Int
//    ): List<AnalysisWithPopularity>

    @Query("""
    SELECT COUNT(DISTINCT s.analysis_id)
    FROM analytics_popular_analysis s
    JOIN topic_analysis ta ON s.analysis_id = ta.analysis_id
    JOIN city_analysis ca ON s.analysis_id = ca.analysis_id
    WHERE ta.topic_id = :topicId
      AND ca.city_id = :cityId
""")
    fun countPopularAnalysesByTopicAndCity(
            topicId: String,
            cityId: String
    ): Int

}