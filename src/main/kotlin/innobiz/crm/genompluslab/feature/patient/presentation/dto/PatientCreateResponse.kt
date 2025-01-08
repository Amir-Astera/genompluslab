package innobiz.crm.genompluslab.feature.patient.presentation.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PatientCreateResponse(
        @JsonProperty("PATIENTID")
        val patientId: String?,
        @JsonProperty("SUCCESS")
        val success: String?
)
