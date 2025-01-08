package innobiz.crm.genompluslab.feature.cart.domain.usecases

import innobiz.crm.genompluslab.feature.cart.domain.services.CartService
import innobiz.crm.genompluslab.feature.cart.presentation.dto.ChangeStatusCartDto
import org.springframework.stereotype.Service

interface ChangeStatusToInUseCase {
    suspend operator fun invoke(dto: ChangeStatusCartDto)
}

@Service
internal class ChangeStatusToInUseCaseImpl(
    private val cartService: CartService
): ChangeStatusToInUseCase {
    override suspend fun invoke(dto: ChangeStatusCartDto) {
        cartService.changeStatusToIn(dto.userId, dto.analyses)
    }
}