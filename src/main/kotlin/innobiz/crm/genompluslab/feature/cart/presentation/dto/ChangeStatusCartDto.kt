package innobiz.crm.genompluslab.feature.cart.presentation.dto

data class ChangeStatusCartDto(
        val userId: String,
        val analyses: Collection<String>
)
