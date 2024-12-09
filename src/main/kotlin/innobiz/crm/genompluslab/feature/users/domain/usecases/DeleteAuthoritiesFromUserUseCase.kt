package com.dev.course.feature.users.domain.usecases

import com.dev.course.feature.authority.domain.errors.AuthorityNotFoundException
import com.dev.course.feature.users.domain.services.UserAggregateService
import com.dev.course.feature.users.presentation.dto.DeleteAuthoritiesFromUserDto
import org.springframework.stereotype.Service

interface DeleteAuthoritiesFromUserUseCase {
    suspend operator fun invoke(userId: String, dto: DeleteAuthoritiesFromUserDto)
}

@Service
internal class DeleteAuthoritiesFromUserUseCaseImpl(
        private val userService: UserAggregateService,
) : DeleteAuthoritiesFromUserUseCase {
    override suspend fun invoke(userId: String, dto: DeleteAuthoritiesFromUserDto) {
            val user = userService.get(userId)
            val authorityIds = dto.authorityIds
            val authorities = userService.getAuthorities(authorityIds)
            if (authorities.size != authorityIds.size) {
                throw AuthorityNotFoundException()
            }
            authorities.forEach { authority->
                user.deleteAuthority(authority.id)
            }
            userService.save(user)
    }
}