package innobiz.crm.genompluslab.feature.authorization.domain.usecases

import com.google.firebase.auth.FirebaseToken
import innobiz.crm.genompluslab.core.security.SessionUser
import innobiz.crm.genompluslab.feature.users.domain.models.UserAggregate
import innobiz.crm.genompluslab.feature.users.domain.services.UserAggregateService
import org.springframework.stereotype.Service

interface SaveSessionUserUseCase {
    suspend operator fun invoke(token: FirebaseToken): SessionUser
}

@Service
internal class SaveSessionUserUseCaseImpl(
    private val userService: UserAggregateService
) : SaveSessionUserUseCase {
    override suspend fun invoke(token: FirebaseToken): SessionUser {
        val phone = token.claims["phone_number"] as? String
        val login = phone ?: token.email
        val aggregate: UserAggregate
        val foundAggregate = userService.getByEmail(login) ?: userService.getByPhone(login) ?: userService.getByLogin(login)
        if (foundAggregate != null) {
            aggregate = foundAggregate
            if (foundAggregate.login != login) {
                aggregate.addLogin(login)
                userService.save(aggregate)
            }
        } else {
            aggregate = UserAggregate(
                name = token.name ?: login,
                login = login,
                email = token.email,
                phone = token.claims["phone_number"] as String,
                surname = token.claims["surname"] as? String
            )
            userService.save(aggregate)
        }

        return SessionUser(
            name = token.name ?: "",
            login = login,
            isEmailVerified = token.isEmailVerified,
            issuer = token.issuer ?: "",
            picture = token.picture ?: "",
            authorities = aggregate.authorities
        )
    }
}