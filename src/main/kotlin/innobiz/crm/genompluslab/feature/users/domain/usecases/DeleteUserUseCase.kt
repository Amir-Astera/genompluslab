package innobiz.crm.genompluslab.feature.users.domain.usecases

import innobiz.crm.genompluslab.feature.users.domain.services.UserAggregateService
import org.springframework.stereotype.Service

interface DeleteUserUseCase {
    suspend operator fun invoke(id: String)
}

@Service
internal class DeleteUserUseCaseImpl(
    private val service: UserAggregateService
) : DeleteUserUseCase {
    override suspend fun invoke(id: String) = service.delete(id)
}