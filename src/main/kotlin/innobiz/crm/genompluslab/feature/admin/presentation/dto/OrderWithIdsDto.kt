package innobiz.crm.genompluslab.feature.admin.presentation.dto

data class OrderWithIdsDto (
        val orderId: String,
        val materials: List<MaterialsWithIds>
)

data class MaterialsWithIds(
        val orderAnalysisId: String,
        val keyId: String,
        val materialId: String,
        val ids: String,
        val analysisCode: String
)