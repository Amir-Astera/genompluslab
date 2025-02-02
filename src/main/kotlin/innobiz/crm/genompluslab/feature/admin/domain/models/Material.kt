package innobiz.crm.genompluslab.feature.admin.domain.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Material(
        val code: String,
        val text: String,
        val keyId: String,
        val materialId: String
)
