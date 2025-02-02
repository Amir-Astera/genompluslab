package innobiz.crm.genompluslab.feature.order.domain.usecases

import innobiz.crm.genompluslab.core.config.enums.CartStatus
import innobiz.crm.genompluslab.core.config.enums.OrderAnalysisStatus
import innobiz.crm.genompluslab.core.config.enums.OrderStatus
import innobiz.crm.genompluslab.core.extension.toModel
import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
import innobiz.crm.genompluslab.feature.analysis.domain.services.AnalysisService
import innobiz.crm.genompluslab.feature.cart.domain.errors.CartNotFoundException
import innobiz.crm.genompluslab.feature.cart.domain.services.CartService
import innobiz.crm.genompluslab.feature.order.domain.errors.OrderCreateException
import innobiz.crm.genompluslab.feature.order.domain.models.Order
import innobiz.crm.genompluslab.feature.order.domain.models.OrderAnalyses
import innobiz.crm.genompluslab.feature.order.domain.services.OrderService
import innobiz.crm.genompluslab.feature.order.presentation.dto.CreateOrderDto
import innobiz.crm.genompluslab.feature.patient.domain.errors.PatientNotFoundException
import innobiz.crm.genompluslab.feature.patient.domain.errors.UserPatientNotFoundException
import innobiz.crm.genompluslab.feature.repositories.PatientRepository
import innobiz.crm.genompluslab.feature.repositories.UserPatientRepository
import innobiz.crm.genompluslab.feature.users.domain.services.UserAggregateService
import org.slf4j.Logger
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
        private val userAggregateService: UserAggregateService,
        private val logger: Logger
): CreateOrderUseCase {
    //TODO тут ошибок не должно быть! Заказ как минимум должен быть сохранен у нас в базе.
    override suspend fun invoke(dto: CreateOrderDto) {
        try {
            val userPatient = userPatientRepository.findByUserId(dto.userId) ?: throw UserPatientNotFoundException(dto.userId)
            val user = userAggregateService.get(userPatient.userId)
            val patient = patientRepository.findById(userPatient.patientId)?.toModel() ?: throw PatientNotFoundException(userPatient.patientId)

            val cart = cartService.get(dto.userId, CartStatus.PAID) ?: throw CartNotFoundException(user.id)
            val analyses = cart.analyses
            val totalPrice = cart.totalSum

            val internalId = orderService.createOrderToLIS(patient) //TODO обрабатывать ошибки со стороны ЛИС, сейчас если ошибка то internalId null и дальше null ошибка выходит
            orderService.setCustomerInfo(internalId)

            val order = Order(
                    user = user,
                    internalId = internalId,
                    totalPrice = totalPrice,
                    status = OrderStatus.ORDERED,
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now()
            )

            val orderAnalyses = analyses.map { analysis ->
                OrderAnalyses(
                        id = "${order.id} - ${analysis.id}",
                        order = order,
                        analysis = analysis,
                        ids = null,
                        price = analysis.price,
                        status = OrderAnalysisStatus.CREATED,
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
            }
            orderService.save(order, orderAnalyses, null)
            cartService.changeStatusToOrdered(user.id, analyses.map { it.id })
        } catch (ex: Exception) {
            logger.error("Failed to create order for userId=${dto.userId}", ex)
            throw OrderCreateException()
        }
    }

}