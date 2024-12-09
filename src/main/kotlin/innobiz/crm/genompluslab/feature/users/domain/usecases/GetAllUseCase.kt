package com.dev.course.feature.users.domain.usecases

import com.dev.course.feature.users.domain.models.UserAggregate
import com.dev.course.feature.users.domain.services.UserAggregateService
import org.springframework.stereotype.Service

interface GetAllUseCase {
    suspend operator fun invoke(email: String?, page: Int, size: Int): Map<String, Any>
}

@Service
internal class GetAllUseCaseImpl(
        private val userService: UserAggregateService
): GetAllUseCase {
    override suspend fun invoke(email: String?, page: Int, size: Int): Map<String, Any> {
       return userService.getAll(email, page, size)
    }
}