package innobiz.crm.genompluslab.feature.order.domain.services

import innobiz.crm.genompluslab.core.config.enums.OrderAnalysisStatus
import innobiz.crm.genompluslab.core.extension.toEntity
import innobiz.crm.genompluslab.core.extension.toModel
import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
import innobiz.crm.genompluslab.feature.order.data.OrderAnalysisEntity
import innobiz.crm.genompluslab.feature.order.domain.errors.OrderAddServiceToMaterialException
import innobiz.crm.genompluslab.feature.order.domain.errors.OrderCreateToLisException
import innobiz.crm.genompluslab.feature.order.domain.errors.OrderNotFoundException
import innobiz.crm.genompluslab.feature.order.domain.models.Order
import innobiz.crm.genompluslab.feature.order.presentation.dto.AddServiceToMaterialDto
import innobiz.crm.genompluslab.feature.order.presentation.dto.LISResponseDto
import innobiz.crm.genompluslab.feature.patient.domain.models.Patient
import innobiz.crm.genompluslab.feature.repositories.OrderAnalysisRepository
import innobiz.crm.genompluslab.feature.repositories.OrderRepository
import innobiz.crm.genompluslab.feature.users.domain.models.UserAggregate
import innobiz.crm.genompluslab.feature.users.domain.services.UserAggregateService
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import org.springframework.stereotype.Service
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.LocalDate
import java.time.LocalDateTime

interface OrderService {
    suspend fun createOrder(userId: String, analyses: Collection<Analysis>, order: Order)
    suspend fun initiatePayment()
    suspend fun createOrderToLIS(patient: Patient): String
    suspend fun addMaterialWithIds(internalId: String, patient: Patient, analysis: Analysis, ids: String)
    suspend fun get(orderId: String): Order
//    suspend fun getPaymentStatus()

}

@Service
internal class OrderServiceImpl(
        private val webClient: WebClient,
        private val orderRepository: OrderRepository,
        private val transactionManager: ReactiveTransactionManager,
        private val orderAnalysisRepository: OrderAnalysisRepository,
        private val userAggregateService: UserAggregateService
): OrderService {
    override suspend fun createOrder(userId: String, analyses: Collection<Analysis>, order: Order) {
        val operator = TransactionalOperator.create(transactionManager)
        orderRepository.saveAll(listOf(order.toEntity(userId))).asFlux()
                .thenMany(
                        orderAnalysisRepository.saveAll(
                                analyses.map {
                                    val id = "${order.id}-${it.id}"
                                    OrderAnalysisEntity(id, order.id, it.id, it.price, OrderAnalysisStatus.CREATED.name, null, LocalDateTime.now(), LocalDateTime.now())
                                }
                        ).asFlux()
                ).`as`(operator::transactional).asFlow().collect {}

    }

    override suspend fun initiatePayment() {
        TODO("Not yet implemented")
    }

    override suspend fun createOrderToLIS(patient: Patient): String {
        val response = webClient
                .get()
                .uri { uriBuilder ->
                    uriBuilder
                            .scheme("https")
                            .host("lcn.bregis.kz")
                            .port(10002)
                            .path("/backend-weblab/api/weblab/CREATE_RESEARCH_ROOT")
                            .queryParam("MAN_ID", "785006")
                            .queryParam("PATIENT_ID", patient.internalId)
                            .queryParam("REGDATE", LocalDate.now())
                            .queryParam("ORDERDATE", LocalDate.now())
                            .queryParam("AGR_ID", "5000")//TODO Идентификатор шифра заказа.
                            .queryParam("ORDERING_ID", "56371172") //TODO Идентификатор внешней направившией организации.
                            .queryParam("DEP_ID", "56371173") //TODO Идентификатор направившего отделения.
                            .queryParam("DIRECT_DOCTOR_ID", "")
                            .queryParam("CATEGORY_ID", "")
                            .queryParam("PRICE_LU_ID", "")
                            .queryParam("CITO", "")
                            .queryParam("STAC_STATUS", "")
                            .queryParam("TRANSP_STATUS", "")
                            .queryParam("CONDITION_ID", "")
                            .queryParam("DIAGNOS", "")
                            .queryParam("INFO_LU_ID", "")
                            .queryParam("LU26_ID", "")
                            .queryParam("PLACEID", "")
                            .queryParam("GOAL_ID", "")
                            .queryParam("NOTE1", "")
                            .queryParam("NOTE2", "")
                            .queryParam("EXT_ID", "")
                            .build()
                }
                .retrieve()
                .awaitBody<List<LISResponseDto>>()
        if (response.isEmpty() || response.first().researchId.isNullOrBlank()) throw OrderCreateToLisException()
        return response.first().researchId!!
    }

    override suspend fun addMaterialWithIds(internalId: String, patient: Patient, analysis: Analysis, ids: String) {
        val material = webClient
                .get()
                .uri { uriBuilder ->
                    uriBuilder
                            .scheme("https")
                            .host("lcn.bregis.kz")
                            .port(10002)
                            .path("/backend-weblab/api/weblab/CREATE_RESEARCH_ADD_MATERIAL_WITH_IDS_DEPID_COLLECTDATA")
                            .queryParam("ROOT_RESEARCH_ID", "785006")
                            .queryParam("SPECIMEN_ID", patient.internalId)
                            .queryParam("IDS", ids)
                            .queryParam("DEPID", LocalDate.now())
                            .queryParam("COLLECT_DATE  ", "5000")
                            .queryParam("COLLECT_PLACE_ID  ", "")
                            .build()
                }
                .retrieve()
                .awaitBody<List<LISResponseDto>>()
        if (material.isEmpty() || material.first().researchId.isNullOrBlank()) throw OrderCreateToLisException()

        val service = webClient
                .get()
                .uri { uriBuilder ->
                    uriBuilder
                            .scheme("https")
                            .host("lcn.bregis.kz")
                            .port(10002)
                            .path("/backend-weblab/api/weblab/CREATE_RESEARCH_ADD_SERVICE")
                            .queryParam("RESEARCH_ID", material.first().researchId)
                            .queryParam("SRVDEP_ID", analysis.code)//TODO нужно уточнить
                            .queryParam("CITO", "")
                            .queryParam("AGR_ID", "5000")
                            .build()
                }
                .retrieve()
                .awaitBody<List<AddServiceToMaterialDto>>()
        if (service.first().success != "success") throw OrderAddServiceToMaterialException()

    }

    override suspend fun get(orderId: String): Order {
        return orderRepository.findById(orderId)?.run {
            toModel(getUser(userId))
        } ?: throw OrderNotFoundException(orderId)
    }

    private suspend fun getUser(userId: String): UserAggregate = userAggregateService.get(userId)


}