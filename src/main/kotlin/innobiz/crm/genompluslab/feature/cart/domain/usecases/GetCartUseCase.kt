package innobiz.crm.genompluslab.feature.cart.domain.usecases

import innobiz.crm.genompluslab.feature.cart.domain.services.CartService
import org.springframework.stereotype.Service

interface GetCartUseCase {
    suspend operator fun invoke(userId: String): Map<String, Any>
}

@Service
internal class GetCartUseCaseImpl(
        private val cartService: CartService
): GetCartUseCase {
    override suspend fun invoke(userId: String): Map<String, Any> {
        return cartService.get(userId)
    }

}