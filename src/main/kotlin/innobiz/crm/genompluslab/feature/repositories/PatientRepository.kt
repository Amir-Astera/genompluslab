package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.feature.patient.data.PatientEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PatientRepository: CoroutineCrudRepository<PatientEntity, String> {
    suspend fun existsByIin(iin: String): Boolean

    suspend fun findByIin(iin: String): PatientEntity?
}