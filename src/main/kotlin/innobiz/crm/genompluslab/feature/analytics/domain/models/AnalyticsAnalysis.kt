package innobiz.crm.genompluslab.feature.analytics.domain.models

import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.UUID

data class AnalyticsAnalysis(
        val id: String = UUID.randomUUID().toString(),
        val analysisId: String,
        val cityId: String,
        val salesCount: Double = 0.0,
        val viewsCount: Double = 0.0,
        val cartCount: Double = 0.0,
        val lastSaleDate: LocalDateTime? = null,
        var version: Long? = null,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null
)
