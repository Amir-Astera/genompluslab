package innobiz.crm.genompluslab.feature.analysis.presentation.dto

data class UpdateAnalysisDto(
        val topicId: String,
        val cityId: String,
        val code: String?,
        val name: String?,
        val material: String?,
        val deadline: String?,
        val price: Double?
)
