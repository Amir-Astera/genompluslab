package innobiz.crm.genompluslab.feature.order.data

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "orders")
class OrderEntity(
        @Id
        val id: String,
        val userId: Long,
        val totalPrice: Double,
        val status: String = "PENDING",
        @Version
        var version: Long?,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime
)