package innobiz.crm.genompluslab.feature.order.presentation.dto

import java.time.LocalDateTime

data class CreateMaterialDto(
        val rootResearchId: String,
        val specimenId: String,
        val ids: String,
        val depid: String,
        val collectDate: LocalDateTime,

)
