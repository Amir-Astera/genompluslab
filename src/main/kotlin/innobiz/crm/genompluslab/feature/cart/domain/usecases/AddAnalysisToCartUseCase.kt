package innobiz.crm.genompluslab.feature.cart.domain.usecases

import innobiz.crm.genompluslab.core.config.enums.CartStatus
import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
import innobiz.crm.genompluslab.feature.analysis.domain.services.AnalysisService
import innobiz.crm.genompluslab.feature.cart.domain.errors.CartAnalysisAlreadyExistException
import innobiz.crm.genompluslab.feature.cart.domain.models.Cart
import innobiz.crm.genompluslab.feature.cart.domain.services.CartService
import innobiz.crm.genompluslab.feature.cart.presentation.dto.AddAnalysisToCartDto
import innobiz.crm.genompluslab.feature.users.domain.errors.UserNotFoundException
import innobiz.crm.genompluslab.feature.users.domain.services.UserAggregateService
import org.springframework.stereotype.Service

interface AddAnalysisToCartUseCase {
    suspend operator fun invoke(dto: AddAnalysisToCartDto)
}

@Service
internal class AddAnalysisToCartUseCaseImpl(
        private val userAggregateService: UserAggregateService,
        private val analysisService: AnalysisService,
        private val cartService: CartService
): AddAnalysisToCartUseCase {
    override suspend fun invoke(dto: AddAnalysisToCartDto) {
        val user = userAggregateService.get(dto.userId)
        val cartAnalyses = cartService.get(user.id, CartStatus.IN)?.analyses
        val analysis = analysisService.get(dto.analysisId)
        if (cartAnalyses != null && cartAnalyses.firstOrNull { it.id == analysis.id } != null) throw CartAnalysisAlreadyExistException()

        cartService.save(
                Cart(
                userId = user.id,
                analysisId = analysis.id,
                status = CartStatus.IN
        ))
    }

}