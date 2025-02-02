package innobiz.crm.genompluslab.feature.order.domain.models

import innobiz.crm.genompluslab.core.config.enums.OrderAnalysisStatus
import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
import org.springframework.data.annotation.Version
import java.time.LocalDateTime

data class OrderAnalyses(
        val id: String,
        val order: Order,
        val analysis: Analysis,
        val ids: String?,
        val price: Double,
        val status: OrderAnalysisStatus,
        var version: Long?,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
)