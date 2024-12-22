package innobiz.crm.genompluslab.feature.users.domain.usecases

import innobiz.crm.genompluslab.feature.users.domain.models.UserAggregate
import innobiz.crm.genompluslab.feature.users.domain.services.UserAggregateService
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