package innobiz.crm.genompluslab.feature.order.presentation.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class LISResponseDto(
        @JsonProperty("RESEARCH_ID")
        val researchId: String?
)
