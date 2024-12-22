package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.feature.analytics.data.AnalyticsAnalysisEntity
import innobiz.crm.genompluslab.feature.analytics.domain.models.AnalysisWithPopularity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AnalyticsAnalysisRepository: CoroutineCrudRepository<AnalyticsAnalysisEntity, String> {
    @Query("""
    SELECT a.id AS analysis_id,
           SUM(s.salesCount * :salesWeight +
               s.viewsCount * :viewsWeight +
               s.cartCount * :cartAddsWeight) *
               CASE
                   WHEN s.lastSaleDate = CURRENT_DATE THEN 1.0
                   WHEN s.lastSaleDate >= CURRENT_DATE - INTERVAL '7 days' THEN 0.8
                   WHEN s.lastSaleDate >= CURRENT_DATE - INTERVAL '30 days' THEN 0.5
                   ELSE 0.2
               END AS popular_score
    FROM AnalyticsAnalysisEntity s
    JOIN CityAnalysisEntity ca ON s.analysisId = ca.analysisId
    WHERE ca.cityId = :cityId
    GROUP BY s.analysisId
    ORDER BY popular_score DESC
    LIMIT :limit
""")
    fun findTopPopularAnalysesByCity(
            cityId: String,
            salesWeight: Double,
            viewsWeight: Double,
            cartAddsWeight: Double,
            limit: Int
    ): List<AnalysisWithPopularity>

    @Query("""
        SELECT a 
        FROM AnalyticsAnalysisEntity a
        WHERE a.analysisId = :analysisId AND a.cityId = :cityId
        FOR UPDATE
    """)
    suspend fun findAndLockAnalysisForUpdate(analysisId: String, cityId: String): AnalyticsAnalysisEntity?
}