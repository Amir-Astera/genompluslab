package innobiz.crm.genompluslab.feature.patient.domain.models

import innobiz.crm.genompluslab.feature.users.domain.models.UserAggregate
import java.time.LocalDate
import java.util.UUID

data class Patient(
        val id: String = UUID.randomUUID().toString(),
        val internalId: String?,
        val iin: String,
        val firstName: String,
        val secondName: String,
        val lastName: String?,
        val birthDay: LocalDate,
        val sex: String,
        val numDoc: String
)
