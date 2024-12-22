package innobiz.crm.genompluslab.feature.users.domain.usecases

import innobiz.crm.genompluslab.feature.authority.domain.errors.AuthorityNotFoundException
import innobiz.crm.genompluslab.feature.authority.domain.services.AuthorityAggregateService
import innobiz.crm.genompluslab.feature.users.domain.errors.UserDuplicateLoginException
import innobiz.crm.genompluslab.feature.users.domain.services.UserAggregateService
import innobiz.crm.genompluslab.feature.users.presentation.dto.UpdateUserDto
import org.springframework.stereotype.Service

interface UpdateUserUseCase {
    suspend operator fun invoke(id: String, dto: UpdateUserDto): String
}

@Service
internal class UpdateUserUseCaseImpl(
        private val userService: UserAggregateService,
        private val authorityService: AuthorityAggregateService,
) : UpdateUserUseCase {
    override suspend fun invoke(id: String, dto: UpdateUserDto): String {

        if (userService.existsWithPhone(dto.phone) || userService.existsWithEmail(dto.email)) {
            throw UserDuplicateLoginException()
        }

        val user = userService.get(id)

        user.update(dto)

        val authorityIds = dto.authorityIds?.map { it } ?: emptyList()
        if (authorityIds.isNotEmpty()) {
            val authorities = authorityService.getAllByIds(authorityIds)
            if (authorities.size != authorityIds.size) {
                throw AuthorityNotFoundException()
            }
            user.updateAuthorities(authorityIds)
        }
        userService.save(user)
        return user.id
    }
}