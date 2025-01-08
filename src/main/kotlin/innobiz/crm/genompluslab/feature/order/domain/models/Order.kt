package innobiz.crm.genompluslab.feature.order.domain.models

import innobiz.crm.genompluslab.core.config.enums.OrderStatus
import innobiz.crm.genompluslab.feature.users.domain.models.UserAggregate
import java.time.LocalDateTime
import java.util.UUID

data class Order(
        val id: String = UUID.randomUUID().toString(),
        val internalId: String,
        val user: UserAggregate,
        val totalPrice: Double,
        val status: OrderStatus = OrderStatus.CREATED,
        val version: Long? = null,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
)
