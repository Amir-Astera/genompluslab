package com.dev.course.feature.users.domain.usecases

import com.dev.course.core.security.SessionUser
import com.dev.course.feature.users.domain.errors.UserNotFoundException
import com.dev.course.feature.users.domain.models.UserAggregate
import com.dev.course.feature.users.domain.services.UserAggregateService
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
