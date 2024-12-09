package com.dev.course.feature.users.domain.usecases

import com.dev.course.feature.users.domain.models.UserAggregate
import com.dev.course.feature.users.domain.services.UserAggregateService
import org.springframework.stereotype.Service

interface GetUserUseCase {
    suspend operator fun invoke(userId: String): UserAggregate
}

@Service
internal class GetUserUseCaseImpl(
    private val service: UserAggregateService
) : GetUserUseCase {
    override suspend fun invoke(userId: String): UserAggregate = service.get(userId)
}