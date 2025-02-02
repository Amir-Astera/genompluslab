package innobiz.crm.genompluslab.feature.patient.domain.usecases

import innobiz.crm.genompluslab.core.security.firebase.FirebaseSecurityUtils
import innobiz.crm.genompluslab.feature.authorization.domain.errors.AuthException
import innobiz.crm.genompluslab.feature.patient.domain.services.PatientService
import innobiz.crm.genompluslab.feature.patient.presentation.dto.GetUserPatientDto
import innobiz.crm.genompluslab.feature.users.domain.errors.UserNotFoundException
import innobiz.crm.genompluslab.feature.users.domain.services.UserAggregateService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange

interface GetPatientUseCase {
    suspend operator fun invoke(exchange: ServerWebExchange): GetUserPatientDto
}

@Service
internal class GetPatientUseCaseImpl(
        private val patientService: PatientService,
        private val userAggregateService: UserAggregateService
): GetPatientUseCase {
    override suspend fun invoke(exchange: ServerWebExchange): GetUserPatientDto {
        val sessionUser = FirebaseSecurityUtils.getUserFromRequest(exchange).awaitSingleOrNull() ?: throw AuthException()
        return withContext(Dispatchers.IO) {
                val user = userAggregateService.getByPhone(sessionUser.login) ?: throw UserNotFoundException()
                val patient = patientService.getByUser(user.id)
                GetUserPatientDto(
                        id = patient.id,
                        iin = patient.iin,
                        firstName = patient.firstName,
                        secondName = patient.secondName,
                        lastName = patient.lastName,
                        birthDay = patient.birthDay,
                        numDoc = patient.numDoc,
                        sex = patient.sex,
                        email = user.email,
                        phone = user.phone,
                )
            }
    }
}