package innobiz.crm.genompluslab.feature.cart.data

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "cart")
class CartEntity(
        @Id
        val id: String,
        val userId: String,
        val analysisId: Collection<String>,
        val quantity: Int,
        @Version
        var version: Long?,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime
)