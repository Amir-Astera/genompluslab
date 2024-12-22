package innobiz.crm.genompluslab.feature.cart.domain.usecases

import innobiz.crm.genompluslab.feature.cart.domain.models.Cart
import innobiz.crm.genompluslab.feature.cart.domain.services.CartService
import innobiz.crm.genompluslab.feature.cart.presentation.dto.AddAnalysisToCartDto
import org.springframework.stereotype.Service

interface AddAnalysisToCartUseCase {
    suspend operator fun invoke(dto: AddAnalysisToCartDto)
}

@Service
internal class AddAnalysisToCartUseCaseImpl(
        private val cartService: CartService
): AddAnalysisToCartUseCase {
    override suspend fun invoke(dto: AddAnalysisToCartDto) {
        cartService.save(
                Cart(
                userId = dto.userId,
                analysisId = dto.analysisId
        ))
    }

}