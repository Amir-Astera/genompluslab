package innobiz.crm.genompluslab.feature.analysis.presentation.dto

data class UpdatePriceByPercentageDto(
        val analysisIds: Collection<String>,
        val price: Int
)
