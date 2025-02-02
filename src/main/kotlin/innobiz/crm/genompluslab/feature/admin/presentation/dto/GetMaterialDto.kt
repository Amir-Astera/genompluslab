package innobiz.crm.genompluslab.feature.admin.presentation.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GetMaterialDto(
        val code: String,
        val text: String,
        val keyid: String,
        @JsonProperty(namespace = "MATERIAL_ID")
        val materialId: String
)
