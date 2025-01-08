package innobiz.crm.genompluslab.feature.order.domain.usecases

import innobiz.crm.genompluslab.core.config.enums.OrderStatus
import innobiz.crm.genompluslab.core.extension.toModel
import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
import innobiz.crm.genompluslab.feature.analysis.domain.services.AnalysisService
import innobiz.crm.genompluslab.feature.cart.domain.services.CartService
import innobiz.crm.genompluslab.feature.order.domain.models.Order
import innobiz.crm.genompluslab.feature.order.domain.services.OrderService
import innobiz.crm.genompluslab.feature.order.presentation.dto.CreateOrderDto
import innobiz.crm.genompluslab.feature.patient.domain.errors.PatientNotFoundException
import innobiz.crm.genompluslab.feature.patient.domain.errors.UserPatientNotFoundException
import innobiz.crm.genompluslab.feature.repositories.PatientRepository
import innobiz.crm.genompluslab.feature.repositories.UserPatientRepository
import innobiz.crm.genompluslab.feature.repositories.UserRepository
import innobiz.crm.genompluslab.feature.users.domain.services.UserAggregateService
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface CreateOrderUseCase {
    suspend operator fun invoke(dto: CreateOrderDto)
}

@Service
internal class CreateOrderUseCaseImpl(
        private val orderService: OrderService,
        private val analysisService: AnalysisService,
        private val cartService: CartService,
        private val userPatientRepository: UserPatientRepository,
        private val patientRepository: PatientRepository,
        private val userAggregateService: UserAggregateService
): CreateOrderUseCase {
    override suspend fun invoke(dto: CreateOrderDto) {
        val userPatient = userPatientRepository.findByUserId(dto.userId) ?: throw UserPatientNotFoundException(dto.userId)
        val user = userAggregateService.get(userPatient.userId)
        val patient = patientRepository.findById(userPatient.patientId)?.toModel() ?: throw PatientNotFoundException(userPatient.patientId)
        val cart = cartService.get(dto.userId)
        val analyses = analysisService.getAllByIds((cart["analyses"] as Collection<Analysis>).map { it.id })
        val totalPrice = cart["totalSum"] as Double
        val internalId = orderService.createOrderToLIS(patient)
        val order = Order(
                user = user,
                internalId = internalId,
                totalPrice = totalPrice,
                status = OrderStatus.CREATED,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
        )
        orderService.createOrder(dto.userId, analyses, order)
        analyses.map { cartService.deleteAnalysis(user.id, it.id) }
    }

}