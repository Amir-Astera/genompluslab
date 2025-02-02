package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.feature.patient.data.UserPatientEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserPatientRepository: CoroutineCrudRepository<UserPatientEntity, String> {
    suspend fun findByPatientId(patientId: String): UserPatientEntity?
    suspend fun findByUserId(userId: String): UserPatientEntity?
}