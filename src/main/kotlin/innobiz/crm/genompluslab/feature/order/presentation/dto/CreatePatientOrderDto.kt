package innobiz.crm.genompluslab.feature.order.presentation.dto

data class CreatePatientOrderDto(
        val patientId: String,
        val orderId: String,
        val ids: String,
        val analysisId: String
)
