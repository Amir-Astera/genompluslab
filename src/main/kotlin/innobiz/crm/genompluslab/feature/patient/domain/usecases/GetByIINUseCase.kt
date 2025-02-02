package innobiz.crm.genompluslab.feature.patient.domain.usecases

import innobiz.crm.genompluslab.feature.patient.domain.services.PatientService
import org.springframework.stereotype.Service

interface GetByIINUseCase {
    suspend operator fun invoke(iin: String): Boolean
}

@Service
internal class GetByIINUseCaseImpl(
     private val patientService: PatientService
): GetByIINUseCase {
    override suspend fun invoke(iin: String): Boolean {
        return patientService.existByIIN(iin)
    }
}