package innobiz.crm.genompluslab.feature.order.data

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "order_analysis")
class OrderAnalysis(
        @Id
        val id: String,
        val orderId: String,
        val analysisId: String,
        val quantity: String,
        val price: String,
        @Version
        var version: Long?,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime
)