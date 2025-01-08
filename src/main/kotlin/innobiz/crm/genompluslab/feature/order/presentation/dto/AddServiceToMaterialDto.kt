package innobiz.crm.genompluslab.feature.order.presentation.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AddServiceToMaterialDto(
        @JsonProperty("SUCCESS")
        val success: String
)
