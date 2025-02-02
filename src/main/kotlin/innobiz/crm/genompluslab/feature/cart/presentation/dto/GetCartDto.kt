package innobiz.crm.genompluslab.feature.cart.presentation.dto

import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis

data class GetCartDto(
        val totalSum: Double,
        val totalCount: Int,
        val analyses: List<Analysis>
)
