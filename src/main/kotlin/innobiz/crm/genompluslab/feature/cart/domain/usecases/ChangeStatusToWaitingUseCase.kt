package innobiz.crm.genompluslab.feature.cart.domain.usecases

import innobiz.crm.genompluslab.feature.cart.domain.services.CartService
import innobiz.crm.genompluslab.feature.cart.presentation.dto.ChangeStatusCartDto
import org.springframework.stereotype.Service

interface ChangeStatusToWaitingUseCase {
    suspend operator fun invoke(dto: ChangeStatusCartDto)
}

@Service
internal class ChangeStatusToWaitingUseCaseImpl(
        private val cartService: CartService
): ChangeStatusToWaitingUseCase {
    override suspend fun invoke(dto: ChangeStatusCartDto) {
        cartService.changeStatusToWaiting(dto.userId, dto.analyses)
    }
}