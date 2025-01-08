package innobiz.crm.genompluslab.feature.patient.data

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "user_patient")
class UserPatientEntity(
        @Id
        val id: String,
        val userId: String,
        val patientId: String,
        @Version
        val version: Long?,
        val createdAt: LocalDateTime
)