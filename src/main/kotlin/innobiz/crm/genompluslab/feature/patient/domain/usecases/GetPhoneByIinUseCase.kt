package innobiz.crm.genompluslab.feature.patient.domain.usecases

import innobiz.crm.genompluslab.feature.patient.domain.errors.PatientNotFoundException
import innobiz.crm.genompluslab.feature.patient.domain.errors.UserPatientNotFoundException
import innobiz.crm.genompluslab.feature.patient.domain.services.PatientService
import innobiz.crm.genompluslab.feature.patient.presentation.dto.PatientPhoneDto
import innobiz.crm.genompluslab.feature.repositories.UserPatientRepository
import innobiz.crm.genompluslab.feature.users.domain.services.UserAggregateService
import org.springframework.stereotype.Service

interface GetPhoneByIinUseCase {
    suspend operator fun invoke(iin: String): PatientPhoneDto
}

@Service
internal class GetPhoneByIinUseCaseImpl(
        private val patientService: PatientService,
        private val userPatientRepository: UserPatientRepository,
        private val userAggregateService: UserAggregateService
): GetPhoneByIinUseCase {
    override suspend fun invoke(iin: String): PatientPhoneDto {
        if (!patientService.existByIIN(iin)) throw PatientNotFoundException(iin)
        val patientId = patientService.get(iin).id
        val userId = userPatientRepository.findByPatientId(patientId)?.userId ?: throw UserPatientNotFoundException("by iin: $iin")
        return PatientPhoneDto(phone = userAggregateService.get(userId).phone)
    }
}