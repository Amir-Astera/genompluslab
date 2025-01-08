package innobiz.crm.genompluslab.feature.cart.domain.usecases

import innobiz.crm.genompluslab.feature.cart.domain.services.CartService
import innobiz.crm.genompluslab.feature.cart.presentation.dto.ChangeStatusCartDto
import org.springframework.stereotype.Service

interface ChangeStatusToPaidUseCase {
    suspend operator fun invoke(dto: ChangeStatusCartDto)
}

@Service
internal class ChangeStatusToPaidUseCaseImpl(
        private val cartService: CartService
): ChangeStatusToPaidUseCase {
    override suspend fun invoke(dto: ChangeStatusCartDto) {
        cartService.changeStatusToPaid(dto.userId, dto.analyses)
    }
}