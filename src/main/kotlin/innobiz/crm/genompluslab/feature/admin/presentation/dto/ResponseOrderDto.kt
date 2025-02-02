package innobiz.crm.genompluslab.feature.admin.presentation.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ResponseOrderDto(
        @JsonProperty("SUCCESS")
        val success: String
)