package innobiz.crm.genompluslab.feature.analysis.presentation.dto

data class AddAnalysisDto(
        val topicId: String,
        val cityId: String,
        val code: String,
        val name: String,
        val material: String,
        val deadline: String,
        val price: Double,
        val description: String
)
