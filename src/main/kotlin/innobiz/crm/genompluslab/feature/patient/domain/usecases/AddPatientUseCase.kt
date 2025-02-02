package innobiz.crm.genompluslab.feature.patient.domain.usecases

import com.google.firebase.auth.UserRecord
import innobiz.crm.genompluslab.core.config.FirebaseConfig
import innobiz.crm.genompluslab.core.config.api.CreateResponseDto
import innobiz.crm.genompluslab.core.config.enums.PatientSex
import innobiz.crm.genompluslab.core.extension.apiFutureToMono
import innobiz.crm.genompluslab.feature.authority.domain.services.AuthorityAggregateService
import innobiz.crm.genompluslab.feature.patient.domain.errors.PatientAlreadyExcitingException
import innobiz.crm.genompluslab.feature.patient.domain.models.Patient
import innobiz.crm.genompluslab.feature.patient.domain.services.PatientService
import innobiz.crm.genompluslab.feature.patient.presentation.dto.UserPatientDto
import innobiz.crm.genompluslab.feature.users.domain.errors.UserDuplicateLoginException
import innobiz.crm.genompluslab.feature.users.domain.models.UserAggregate
import innobiz.crm.genompluslab.feature.users.domain.services.UserAggregateService
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

interface AddPatientUseCase {
    suspend operator fun invoke(dto: UserPatientDto): CreateResponseDto
}

@Service
internal class AddPatientUseCaseImpl(
        private val patientService: PatientService,
        private val userService: UserAggregateService,
        private val authorityService: AuthorityAggregateService,
        @Autowired
        private val firebaseConfig: FirebaseConfig,
): AddPatientUseCase {
    override suspend fun invoke(dto: UserPatientDto): CreateResponseDto {
        if(patientService.existByIIN(dto.iin)) throw PatientAlreadyExcitingException(dto.iin)

        if (userService.existsWithPhone(dto.phone) || userService.existsWithEmail(dto.email)) {
            throw UserDuplicateLoginException()
        }
                //TODO numDoc уникальный должен быть
        //TODO проверку на пол 0 или 1
        val patient = Patient(
                iin = dto.iin,
                firstName = dto.firstName,
                secondName = dto.secondName,
                lastName = dto.lastName,
                birthDay = dto.birthDay,
                sex = PatientSex.fromCode(dto.sex).name,
                numDoc = dto.numDoc,
                internalId = null
        )

        val authorities = authorityService.get("658535c1-4cbf-473d-ad5b-9df02d091254")
        val user = UserAggregate(
                name = dto.firstName,
                login = dto.phone,
                surname = dto.secondName,
                email = dto.email,
                phone = dto.phone,
        )
        user.addAuthority(authorities.id)

        val internalId = patientService.createPatientToLis(patient, user)

        val userFirebase = UserRecord.CreateRequest()
                .setPhoneNumber(user.phone)
                .setEmail(user.email)
                .setPassword(dto.password)
                .setUid(user.id)

        firebaseConfig.auth().createUserAsync(userFirebase)


        userService.save(user).also {
            val id = patientService.save(
                    patient.copy(internalId = internalId),
                    user
            )
            return CreateResponseDto(id)
        }
    }
}