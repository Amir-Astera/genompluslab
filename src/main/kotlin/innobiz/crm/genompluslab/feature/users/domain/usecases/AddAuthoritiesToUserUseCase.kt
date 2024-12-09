package com.dev.course.feature.users.domain.usecases

import com.dev.course.feature.authority.domain.errors.AuthorityNotFoundException
import com.dev.course.feature.users.domain.services.UserAggregateService
import com.dev.course.feature.users.presentation.dto.AddAuthoritiesToUserDto
import org.springframework.stereotype.Service

interface AddAuthoritiesToUserUseCase {
    suspend operator fun invoke(userId: String, dto: AddAuthoritiesToUserDto)
}

@Service
internal class AddAuthoritiesToUserUseCaseImpl(
        private val userService: UserAggregateService,
) : AddAuthoritiesToUserUseCase {
    override suspend fun invoke(userId: String, dto: AddAuthoritiesToUserDto) {
        val user = userService.get(userId)

        val authorityIds = dto.authorityIds
        val authorities = userService.getAuthorities(authorityIds)
        if (authorities.size != authorityIds.size) {
            throw AuthorityNotFoundException()
        }

        user.updateAuthorities(authorities.map { it.id })
        return userService.save(user)
    }
}