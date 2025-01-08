package innobiz.crm.genompluslab.feature.order.data

import innobiz.crm.genompluslab.core.config.enums.OrderStatus
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "orders")
class OrderEntity(
        @Id
        val id: String,
        val internalId: String,
        val userId: String,
        val totalPrice: Double,
        val status: OrderStatus,
        @Version
        var version: Long?,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime
)