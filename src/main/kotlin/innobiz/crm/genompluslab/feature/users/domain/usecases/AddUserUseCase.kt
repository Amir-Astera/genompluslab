package com.dev.course.feature.users.domain.usecases

import com.dev.course.core.config.api.CreateResponseDto
import com.dev.course.feature.authority.domain.errors.AuthorityNotFoundException
import com.dev.course.feature.authority.domain.services.AuthorityAggregateService
import com.dev.course.feature.users.domain.errors.UserDuplicateLoginException
import com.dev.course.feature.users.domain.models.UserAggregate
import com.dev.course.feature.users.domain.services.UserAggregateService
import com.dev.course.feature.users.presentation.dto.CreateUserDto
import com.google.firebase.auth.UserRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.dev.course.core.config.FirebaseConfig

interface AddUserUseCase {
    suspend operator fun invoke(dto: CreateUserDto): CreateResponseDto
}

@Service
internal class AddUserUseCaseImpl(
        private val userService: UserAggregateService,
        private val authorityService: AuthorityAggregateService,
        @Autowired
        private val firebaseConfig: FirebaseConfig
) : AddUserUseCase {
    override suspend fun invoke(dto: CreateUserDto): CreateResponseDto {

        if (userService.existsWithLogin(dto.login) || userService.existsWithPhone(dto.phone) || userService.existsWithEmail(dto.email)) {
            throw UserDuplicateLoginException()
        }
        val user = UserAggregate(
            name = dto.name,
            login = dto.login ?: (dto.email ?: dto.phone ?: ""),
            surname = dto.surname,
            email = dto.email,
            phone = dto.phone
        )
        val authorityIds = dto.authorityIds?.map { it } ?: emptyList()
        if (authorityIds.isNotEmpty()) {
            val authorities = authorityService.getAllByIds(authorityIds)
            if (authorities.size != authorityIds.size) {
                throw AuthorityNotFoundException()
            }
            authorityIds.forEach { user.addAuthority(it) }
        }
        val userFirebase = UserRecord.CreateRequest()
                .setEmail(user.email)
                .setPassword(dto.password)
                .setUid(user.id)
        firebaseConfig.auth().createUser(userFirebase)
         userService. save(user)
        return CreateResponseDto(user.id)
    }
}
