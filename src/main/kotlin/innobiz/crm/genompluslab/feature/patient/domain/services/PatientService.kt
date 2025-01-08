package innobiz.crm.genompluslab.feature.patient.domain.services

import innobiz.crm.genompluslab.core.config.enums.Sex
import innobiz.crm.genompluslab.core.extension.toEntity
import innobiz.crm.genompluslab.core.extension.toModel
import innobiz.crm.genompluslab.feature.patient.data.UserPatientEntity
import innobiz.crm.genompluslab.feature.patient.domain.errors.PatientCreateException
import innobiz.crm.genompluslab.feature.patient.domain.errors.PatientNotFoundException
import innobiz.crm.genompluslab.feature.patient.domain.models.Patient
import innobiz.crm.genompluslab.feature.patient.presentation.dto.PatientCreateResponse
import innobiz.crm.genompluslab.feature.repositories.PatientRepository
import innobiz.crm.genompluslab.feature.repositories.UserPatientRepository
import innobiz.crm.genompluslab.feature.users.domain.models.UserAggregate
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import org.springframework.cglib.core.Local
import org.springframework.stereotype.Service
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.LocalDateTime

interface PatientService {
    suspend fun save(patient: Patient, user: UserAggregate): String
    suspend fun findByIIN(iin: String): Boolean
    suspend fun get (iin: String): Patient
    suspend fun createPatientToLis(patient: Patient, user: UserAggregate): String
}
@Service
internal class PatientServiceImpl(
        private val patientRepository: PatientRepository,
        private val transactionManager: ReactiveTransactionManager,
        private val userPatientRepository: UserPatientRepository,
        private val webClient: WebClient
): PatientService {
    override suspend fun save(patient: Patient, user: UserAggregate): String {
        val operator = TransactionalOperator.create(transactionManager)
        patientRepository.saveAll(listOf(patient.toEntity())).asFlux()
                .thenMany(
                        userPatientRepository.saveAll(
                                    listOf(UserPatientEntity("${user.id}-${patient.id}", user.id, patient.id, user.version, LocalDateTime.now()))
                        ).asFlux()
                ).`as`(operator::transactional).asFlow().collect {}
        return patient.id
    }
    override suspend fun findByIIN(iin: String): Boolean {
        return patientRepository.existsByIin(iin)
    }

    override suspend fun get(iin: String): Patient {
        return patientRepository.findByIin(iin)?.run {
            toModel()
        } ?: throw PatientNotFoundException(iin)
    }

    override suspend fun createPatientToLis(patient: Patient, user: UserAggregate): String {
        //TODO реализовать поиск пациента в базе ЛИС, если есть то из базы взять данные пациента и сохранить у себя
        val response = webClient
                .get()
                .uri { uriBuilder ->
                    uriBuilder
                            .scheme("https")
                            .host("lcn.bregis.kz")
                            .port(10002)
                            .path("/backend-weblab/api/weblab/CREATE_PATCARD")
                            .queryParam("PATIENTID", "")
                            .queryParam("LASTNAME", patient.lastName)
                            .queryParam("FIRSTNAME", patient.firstName)
                            .queryParam("SECONDNAME", patient.secondName)
                            .queryParam("BIRTHDAY", patient.birthDay)
                            .queryParam("SEX", Sex.valueOf(patient.sex))
                            .queryParam("SNILS", patient.iin)
                            .queryParam("AREAID", "")
                            .queryParam("HOMEPHONE", "")
                            .queryParam("CELLPHONE", user.phone)
                            .queryParam("RELPHONE", "")
                            .queryParam("FAXMAIL", "")
                            .queryParam("COMMENT", "Данный пользователь создан автоматический при регистрации.")
                            .queryParam("CATEGID", "")
                            .queryParam("SERDOC", "")
                            .queryParam("NUMDOC", patient.numDoc)
                            .queryParam("WHODOC", "")
                            .queryParam("DOC_ISSUED_DATE", "")
                            .queryParam("DOC_TRANSLIT_LNAME", "")
                            .queryParam("DOC_TRANSLIT_FNAME", "")
                            .queryParam("DOC_TRANSLIT_SNAME", "")
                            .queryParam("DOC_TRANSLIT_WHODOC", "")
                            .queryParam("SOCSTATID", "2321")//TODO нужно узнать
                            .queryParam("WORKPLACE", "")
                            .queryParam("WORKPOSITION", "")
                            .queryParam("AGRID", "5000")
                            .queryParam("INSURETYPEID", "")
                            .queryParam("POLTYPEID", "")
                            .queryParam("POLSER", "")
                            .queryParam("POLNUM", "")
                            .queryParam("BGNDAT", "")
                            .queryParam("ENDDAT", "")
                            .queryParam("POLADR", "")
                            .queryParam("DOCUMENT_TYPEID", "")
                            .build()
                }
                .retrieve()
                .awaitBody<List<PatientCreateResponse>>()
        if (response.isEmpty()) throw PatientCreateException(patient.iin)
        if (response.first().success != "success") throw PatientCreateException(patient.iin)
        if (response.first().patientId.isNullOrBlank()) throw PatientCreateException(patient.iin)
        return response.first().patientId!!
    }


}