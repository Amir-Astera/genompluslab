package innobiz.crm.genompluslab.feature.cart.domain.models

data class CartDetailsDto(
        val totalCount: Long,
        val totalSum: Double,
        val userId: String
)