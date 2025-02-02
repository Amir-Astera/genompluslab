package innobiz.crm.genompluslab.feature.order.data

import innobiz.crm.genompluslab.core.config.enums.OrderAnalysisStatus
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "order_analysis")
class OrderAnalysisEntity(
        @Id
        val id: String,
        val orderId: String,
        val analysisId: String,
        val ids: String?,
        val price: Double,
        val status: OrderAnalysisStatus,
        @Version
        var version: Long?,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime? = null
)