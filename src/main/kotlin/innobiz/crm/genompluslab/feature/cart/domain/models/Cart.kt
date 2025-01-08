package innobiz.crm.genompluslab.feature.cart.domain.models

import innobiz.crm.genompluslab.core.config.enums.CartStatus
import java.time.LocalDateTime
import java.util.*

data class Cart(
        val id: String = UUID.randomUUID().toString(),
        val userId: String,
        val analysisId: String,
        val status: CartStatus,
        val version: Long? = null,
        val createdAt: LocalDateTime = LocalDateTime.now(),
        val updatedAt: LocalDateTime? = LocalDateTime.now()
)
