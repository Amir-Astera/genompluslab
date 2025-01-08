package innobiz.crm.genompluslab.feature.patient.data

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime
@Table(name = "patient")
class PatientEntity(
        @Id
        val id: String,
        val internalId: String,
        val iin: String,
        val firstName: String,
        val secondName: String,
        val lastName: String?,
        val birthDay: LocalDate,
        val sex: String,
        val numDoc: String,
        @Version
        val version: Long?,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
)