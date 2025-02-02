package innobiz.crm.genompluslab.feature.cart.domain.usecases

import innobiz.crm.genompluslab.feature.cart.domain.services.CartService
import innobiz.crm.genompluslab.feature.users.domain.errors.UserNotFoundException
import innobiz.crm.genompluslab.feature.users.domain.services.UserAggregateService
import org.springframework.stereotype.Service

interface DeleteAnalysisInCartUseCase {
    suspend operator fun invoke(login: String, analysisId: String)
}

@Service
internal class DeleteAnalysisInCartUseCaseImpl(
        private val cartService: CartService,
        private val userAggregateService: UserAggregateService
): DeleteAnalysisInCartUseCase {
    override suspend fun invoke(login: String, analysisId: String) {
        val user = userAggregateService.getByPhone(login) ?: throw UserNotFoundException()
        cartService.deleteAnalysis(user.id, analysisId)
    }

}