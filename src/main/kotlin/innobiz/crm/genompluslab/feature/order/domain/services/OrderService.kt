package innobiz.crm.genompluslab.feature.order.domain.services

import innobiz.crm.genompluslab.core.config.enums.OrderAnalysisStatus
import innobiz.crm.genompluslab.core.extension.toEntity
import innobiz.crm.genompluslab.core.extension.toModel
import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
import innobiz.crm.genompluslab.feature.order.data.OrderAnalysisEntity
import innobiz.crm.genompluslab.feature.order.domain.errors.OrderAddServiceToMaterialException
import innobiz.crm.genompluslab.feature.order.domain.errors.OrderCreateToLisException
import innobiz.crm.genompluslab.feature.order.domain.errors.OrderNotFoundException
import innobiz.crm.genompluslab.feature.order.domain.errors.OrderSetCustomerException
import innobiz.crm.genompluslab.feature.order.domain.models.Order
import innobiz.crm.genompluslab.feature.order.domain.models.OrderAnalyses
import innobiz.crm.genompluslab.feature.order.presentation.dto.AddServiceToMaterialDto
import innobiz.crm.genompluslab.feature.order.presentation.dto.LISResponseDto
import innobiz.crm.genompluslab.feature.order.presentation.dto.SetCustomerIfnfoDto
import innobiz.crm.genompluslab.feature.patient.domain.models.Patient
import innobiz.crm.genompluslab.feature.repositories.AnalysisRepository
import innobiz.crm.genompluslab.feature.repositories.OrderAnalysisRepository
import innobiz.crm.genompluslab.feature.repositories.OrderRepository
import innobiz.crm.genompluslab.feature.users.domain.models.UserAggregate
import innobiz.crm.genompluslab.feature.users.domain.services.UserAggregateService
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Service
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface OrderService {
    suspend fun save(order: Order, orderAnalysesWithoutIds: Collection<OrderAnalyses>?, orderAnalysesWithIds: List<OrderAnalyses>?)
    suspend fun updateStatusToProcessing(order: Order)
    suspend fun initiatePayment()
    suspend fun createOrderToLIS(patient: Patient): String
    suspend fun setCustomerInfo(internalId: String)
    suspend fun get(orderId: String): Order
//    suspend fun getPaymentStatus()

}

@Service
internal class OrderServiceImpl(
        private val webClient: WebClient,
        private val orderRepository: OrderRepository,
        private val transactionManager: ReactiveTransactionManager,
        private val orderAnalysisRepository: OrderAnalysisRepository,
        private val userAggregateService: UserAggregateService,
        private val analysisRepository: AnalysisRepository
): OrderService {
    override suspend fun save(order: Order, orderAnalysesWithoutIds: Collection<OrderAnalyses>?, orderAnalysesWithIds: List<OrderAnalyses>?) {
        val operator = TransactionalOperator.create(transactionManager)

        val analysesToSave = orderAnalysesWithIds ?: orderAnalysesWithoutIds.orEmpty()
        val orderAnalysesEntity = orderAnalysisRepository
                .saveAll(analysesToSave.map { it.toEntity() })

        orderRepository.saveAll(listOf(order.toEntity(order.user.id))).asFlux()
                .thenMany(orderAnalysesEntity.asFlux())
                .`as`(operator::transactional).asFlow().collect {}

    }

    override suspend fun updateStatusToProcessing(order: Order) {
        orderRepository.save(order.toEntity(order.user.id))
    }
    override suspend fun initiatePayment() {
        TODO("Not yet implemented")
    }

    override suspend fun createOrderToLIS(patient: Patient): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'+'HH:mm:ss")
        val currentDateTime = LocalDateTime.now().format(formatter)
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
                            .queryParam("REGDATE", currentDateTime)
                            .queryParam("ORDERDATE", currentDateTime)
                            .queryParam("AGR_ID", "5000")//TODO Идентификатор шифра заказа.
                            .queryParam("ORDERING_ID", "55395000") //TODO Идентификатор внешней направившией организации.
                            .queryParam("DEP_ID", "") //TODO Идентификатор направившего отделения.
                            .queryParam("DIRECT_DOCTOR_ID", "56001000")
                            .queryParam("CATEGORY_ID", "")
                            .queryParam("PRICE_LU_ID", "")
                            .queryParam("CITO", "0")
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
                .header("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJuYmYiOjE3MzgwOTIzMzcsImRhdGEiOnsiaWQiOjc4NTAyOCwidXVpZCI6IjVmYTQ2MTcwLTE3NWItNGZhMi1hYzIwLTA5ODBmYjU3NGM5ZCJ9LCJleHAiOjE3MzgwOTU5MzcsImlhdCI6MTczODA5MjMzN30.XIZE0OHq-s0aQI0-dvVQwG8vVThEzsMJzLRMjg1WBs0")
                .retrieve()
                .awaitBody<List<LISResponseDto>>()
        if (response.isEmpty() || response.first().researchId.isNullOrBlank()) throw OrderCreateToLisException()
        return response.first().researchId!!
    }

    override suspend fun setCustomerInfo(internalId: String) {
        val response = webClient
                .get()
                .uri { uriBuilder ->
                    uriBuilder
                            .scheme("https")
                            .host("lcn.bregis.kz")
                            .port(10002)
                            .path("/backend-weblab/api/weblab/CREATE_RESEARCH_SET_CUSTOMER_INFO")
                            .queryParam("RESEARCH_ID", internalId)
                            .queryParam("AGR_ID", "5000")
                            .queryParam("EXT_DIR", "55395000")
                            .queryParam("WHO_DIR", "56001000")
                            .queryParam("INT_DIR", "")
                            .build()
                }
                .header("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJuYmYiOjE3MzgwOTIzMzcsImRhdGEiOnsiaWQiOjc4NTAyOCwidXVpZCI6IjVmYTQ2MTcwLTE3NWItNGZhMi1hYzIwLTA5ODBmYjU3NGM5ZCJ9LCJleHAiOjE3MzgwOTU5MzcsImlhdCI6MTczODA5MjMzN30.XIZE0OHq-s0aQI0-dvVQwG8vVThEzsMJzLRMjg1WBs0")
                .retrieve()
                .awaitBody<List<SetCustomerIfnfoDto>>()
        if (response.isEmpty()) throw OrderSetCustomerException()
        if (response.first().success != "1") throw OrderSetCustomerException()

    }

    override suspend fun get(orderId: String): Order {
        return orderRepository.findById(orderId)?.run {
            toModel(getUser(userId))
        } ?: throw OrderNotFoundException(orderId)
    }

    private suspend fun getUser(userId: String): UserAggregate = userAggregateService.get(userId)

}