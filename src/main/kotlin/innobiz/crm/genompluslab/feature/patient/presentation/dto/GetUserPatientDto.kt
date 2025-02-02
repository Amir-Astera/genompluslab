package innobiz.crm.genompluslab.feature.patient.presentation.dto

import java.time.LocalDate

data class GetUserPatientDto(
        val id: String,
        val iin: String,
        val firstName: String,
        val secondName: String,
        val lastName: String?,
        val birthDay: LocalDate,
        val sex: String,
        val numDoc: String,
        val email: String,
        val phone: String,
)
