package innobiz.crm.genompluslab.feature.analytics.data

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "analytics_popular_analysis")
class AnalyticsAnalysisEntity(
        @Id
        val id: String,
        val analysisId: String,
        val cityId: String,
        val salesCount: Double,
        val viewsCount: Double,
        val cartCount: Double,
        val lastSaleDate: LocalDateTime? = null,
        @Version
        var version: Long?,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime
)