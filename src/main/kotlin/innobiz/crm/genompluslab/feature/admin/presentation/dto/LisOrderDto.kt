package innobiz.crm.genompluslab.feature.admin.presentation.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class LisOrderDto(
        @JsonProperty("RESEARCH_ID")
        val researchId: String
)