package innobiz.crm.genompluslab.feature.users.domain.usecases

import innobiz.crm.genompluslab.core.security.SessionUser
import innobiz.crm.genompluslab.feature.users.domain.errors.UserNotFoundException
import innobiz.crm.genompluslab.feature.users.domain.models.UserAggregate
import innobiz.crm.genompluslab.feature.users.domain.services.UserAggregateService
import org.springframework.stereotype.Service

interface GetUserBySessionUseCase {
	suspend operator fun invoke(sessionUser: SessionUser): UserAggregate
}

@Service
internal class GetUserBySessionUseCaseImpl(
	private val userService: UserAggregateService
) : GetUserBySessionUseCase {

	override suspend fun invoke(sessionUser: SessionUser): UserAggregate =
			userService.getByLogin(sessionUser.login) ?: throw UserNotFoundException()
}
