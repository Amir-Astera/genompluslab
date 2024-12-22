package innobiz.crm.genompluslab.feature.cart.domain.models

import java.time.LocalDateTime
import java.util.*

data class Cart(
        val id: String = UUID.randomUUID().toString(),
        val userId: String,
        val analysisId: String
)
