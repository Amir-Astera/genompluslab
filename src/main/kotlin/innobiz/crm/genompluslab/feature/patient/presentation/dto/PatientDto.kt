package innobiz.crm.genompluslab.feature.patient.presentation.dto

import java.time.LocalDate

data class PatientDto(
        val iin: String,
        val firstName: String,
        val secondName: String,
        val lastName: String?,
        val birthDay: LocalDate,
        val sex: String,
)
