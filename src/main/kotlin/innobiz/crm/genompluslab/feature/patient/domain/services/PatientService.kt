package innobiz.crm.genompluslab.feature.patient.domain.services

import innobiz.crm.genompluslab.core.config.enums.PatientSex
import innobiz.crm.genompluslab.core.extension.toEntity
import innobiz.crm.genompluslab.core.extension.toModel
import innobiz.crm.genompluslab.feature.patient.data.UserPatientEntity
import innobiz.crm.genompluslab.feature.patient.domain.errors.PatientAddAgrException
import innobiz.crm.genompluslab.feature.patient.domain.errors.PatientCreateException
import innobiz.crm.genompluslab.feature.patient.domain.errors.PatientNotFoundException
import innobiz.crm.genompluslab.feature.patient.domain.models.Patient
import innobiz.crm.genompluslab.feature.patient.presentation.dto.PatientAddAgrDto
import innobiz.crm.genompluslab.feature.patient.presentation.dto.PatientCreateResponse
import innobiz.crm.genompluslab.feature.repositories.PatientRepository
import innobiz.crm.genompluslab.feature.repositories.UserPatientRepository
import innobiz.crm.genompluslab.feature.users.domain.models.UserAggregate
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitEntity
import org.springframework.web.reactive.function.client.awaitExchange
import java.time.LocalDateTime
import kotlin.math.log
import kotlin.random.Random

interface PatientService {
    suspend fun save(patient: Patient, user: UserAggregate): String
    suspend fun existByIIN(iin: String): Boolean
    suspend fun get (iin: String): Patient
    suspend fun createPatientToLis(patient: Patient, user: UserAggregate): String
    suspend fun getByUser(userId: String): Patient
}
@Service
internal class PatientServiceImpl(
        private val patientRepository: PatientRepository,
        private val transactionManager: ReactiveTransactionManager,
        private val userPatientRepository: UserPatientRepository,
        private val webClient: WebClient,
        private val logger: Logger
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
    override suspend fun existByIIN(iin: String): Boolean {
        return patientRepository.existsByIin(iin)
    }

    override suspend fun get(iin: String): Patient {
        return patientRepository.findByIin(iin)?.run {
            toModel()
        } ?: throw PatientNotFoundException(iin)
    }

    override suspend fun createPatientToLis(patient: Patient, user: UserAggregate): String {
        //TODO реализовать поиск пациента в базе ЛИС, если есть то из базы взять данные пациента и сохранить у себя
        //TODO если такой пациент есть то не создавать а просто вернуть ее, но это должно быть в юзкейсе
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
                            .queryParam("SEX", PatientSex.fromValue(patient.sex))
                            .queryParam("SNILS", patient.iin)
                            .queryParam("AREAID", "")
                            .queryParam("HOMEPHONE", "")
                            .queryParam("CELLPHONE", user.phone)
                            .queryParam("RELPHONE", "")
                            .queryParam("FAXMAIL", "")
                            .queryParam("COMMENT", "Данный пользователь создан автоматический при регистрации.")
                            .queryParam("CATEGID", "")
                            .queryParam("DOCUMENTID", "")
                            .queryParam("SERDOC", "")
                            .queryParam("NUMDOC", patient.numDoc)
                            .queryParam("WHODOC", "")
                            .queryParam("DOC_ISSUED_DATE", "")
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
                            .queryParam("ADDRTYPEID", "")
                            .queryParam("REGIONID", "")
                            .queryParam("CITYID", "")
                            .queryParam("CITY", "")
                            .queryParam("STREETID", "")
                            .queryParam("STREET", "")
                            .queryParam("HOUSE", "")
                            .queryParam("CORP", "")
                            .queryParam("FLAT", "")
                            .queryParam("LIVES", "")
                            .build()
                }
                .header("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJuYmYiOjE3MzgwOTIzMzcsImRhdGEiOnsiaWQiOjc4NTAyOCwidXVpZCI6IjVmYTQ2MTcwLTE3NWItNGZhMi1hYzIwLTA5ODBmYjU3NGM5ZCJ9LCJleHAiOjE3MzgwOTU5MzcsImlhdCI6MTczODA5MjMzN30.XIZE0OHq-s0aQI0-dvVQwG8vVThEzsMJzLRMjg1WBs0")
                .retrieve()
                .awaitBody<List<PatientCreateResponse>>()
        if (response.isEmpty()) throw PatientCreateException(patient.iin)
        if (response.first().success != "1") throw PatientCreateException(patient.iin)
        if (response.first().patientId.isNullOrBlank()) throw PatientCreateException(patient.iin)

        webClient
                .get()
                .uri { uriBuilder ->
                    uriBuilder
                            .scheme("https")
                            .host("lcn.bregis.kz")
                            .port(10002)
                            .path("/backend-weblab/api/weblab/CREATE_AGRPAT")
                            .queryParam("PATIENTID", response.first().patientId)
                            .queryParam("MAN_ID", "785006")//TODO нужно зашить в проперти либо обновлять
                            .queryParam("AGRID", "5000")
                            .build()
                }
                .header("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJuYmYiOjE3MzgwOTIzMzcsImRhdGEiOnsiaWQiOjc4NTAyOCwidXVpZCI6IjVmYTQ2MTcwLTE3NWItNGZhMi1hYzIwLTA5ODBmYjU3NGM5ZCJ9LCJleHAiOjE3MzgwOTU5MzcsImlhdCI6MTczODA5MjMzN30.XIZE0OHq-s0aQI0-dvVQwG8vVThEzsMJzLRMjg1WBs0")
                .awaitExchange {
                    if (it.statusCode() != HttpStatus.OK) {
                        throw PatientAddAgrException(patient.id)
                    }
                }

        return response.first().patientId!!
    }

    override suspend fun getByUser(userId: String): Patient {
        val patientId = userPatientRepository.findByUserId(userId)?.patientId ?: throw PatientNotFoundException("Patient by User id: $userId not found!")
        return patientRepository.findById(patientId)?.toModel() ?: throw PatientNotFoundException(patientId)
    }

}