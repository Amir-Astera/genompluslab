package innobiz.crm.genompluslab.feature.cart.domain.usecases

import innobiz.crm.genompluslab.core.config.enums.CartStatus
import innobiz.crm.genompluslab.core.security.firebase.FirebaseSecurityUtils
import innobiz.crm.genompluslab.feature.authorization.domain.errors.AuthException
import innobiz.crm.genompluslab.feature.cart.domain.errors.CartNotFoundException
import innobiz.crm.genompluslab.feature.cart.domain.services.CartService
import innobiz.crm.genompluslab.feature.cart.presentation.dto.GetCartDto
import innobiz.crm.genompluslab.feature.users.domain.errors.UserNotFoundException
import innobiz.crm.genompluslab.feature.users.domain.services.UserAggregateService
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange

interface GetCartUseCase {
    suspend operator fun invoke(exchange: ServerWebExchange): GetCartDto
}

@Service
internal class GetCartUseCaseImpl(
        private val cartService: CartService,
        private val userAggregateService: UserAggregateService
): GetCartUseCase {
    override suspend fun invoke(exchange: ServerWebExchange): GetCartDto {
        val sessionUser = FirebaseSecurityUtils.getUserFromRequest(exchange).awaitSingleOrNull() ?: throw AuthException()
        val user = userAggregateService.getByPhone(sessionUser.login) ?: throw UserNotFoundException()
        return cartService.get(user.id, CartStatus.IN) ?: throw CartNotFoundException(user.id)
    }
}