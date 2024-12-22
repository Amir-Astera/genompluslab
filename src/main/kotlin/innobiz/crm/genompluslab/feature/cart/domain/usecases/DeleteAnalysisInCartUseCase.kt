package innobiz.crm.genompluslab.feature.cart.domain.usecases

import innobiz.crm.genompluslab.feature.cart.domain.services.CartService
import org.springframework.stereotype.Service

interface DeleteAnalysisInCartUseCase {
    suspend operator fun invoke(userId: String, analysisId: String)
}

@Service
internal class DeleteAnalysisInCartUseCaseImpl(
        private val cartService: CartService
): DeleteAnalysisInCartUseCase {
    override suspend fun invoke(userId: String, analysisId: String) {
        cartService.deleteAnalysis(userId, analysisId)
    }

}