package innobiz.crm.genompluslab.feature.users.domain.services

import innobiz.crm.genompluslab.core.extension.toEntity
import innobiz.crm.genompluslab.core.extension.toModel
import innobiz.crm.genompluslab.core.security.SessionUser
import innobiz.crm.genompluslab.feature.authority.data.entity.UserAuthorityEntity
import innobiz.crm.genompluslab.feature.repositories.UserAuthorityRepository
import innobiz.crm.genompluslab.feature.authority.domain.models.Authority
import innobiz.crm.genompluslab.feature.patient.data.UserPatientEntity
import innobiz.crm.genompluslab.feature.repositories.UserRepository
import innobiz.crm.genompluslab.feature.users.domain.errors.AdminAuthorityNotFoundException
import innobiz.crm.genompluslab.feature.users.domain.errors.UserNotFoundException
import innobiz.crm.genompluslab.feature.repositories.AuthorityRepository
import innobiz.crm.genompluslab.feature.repositories.UserPatientRepository
import innobiz.crm.genompluslab.feature.users.domain.models.UserAggregate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator

interface UserAggregateService {
    suspend fun save(user: UserAggregate)
    suspend fun get(id: String): UserAggregate
    suspend fun delete(id: String)
    suspend fun getAuthorities(authorityIds: List<String>): List<Authority>
    suspend fun getByLogin(login: String): UserAggregate?
    suspend fun getByPhone(phone: String): UserAggregate?
    suspend fun getByEmail(email: String): UserAggregate?
    suspend fun existsWithLogin(login: String?): Boolean
    suspend fun existsWithEmail(email: String?): Boolean
    suspend fun existsWithPhone(phone: String?): Boolean
    suspend fun checkAdminPrivilegesBySession(sessionUser: SessionUser)
    suspend fun getAll(email: String?, page: Int, size: Int): Map<String, Any>
}

@Service
class UserAggregateServiceImpl(
        private val userRepository: UserRepository,
        private val transactionManager: ReactiveTransactionManager,
        private val userAuthorityRepository: UserAuthorityRepository,
        private val authorityRepository: AuthorityRepository
): UserAggregateService {

    override suspend fun save(user: UserAggregate) {
        val operator = TransactionalOperator.create(transactionManager)
        userRepository.saveAll(listOf(user.toEntity())).asFlux()
            .thenMany(
                userAuthorityRepository.saveAll(user.authorities.map {
                    val id = "${user.id}-${it.id}"
                    UserAuthorityEntity(id, user.id, it.id, it.version, it.createdAt, it.updatedAt)
                }).asFlux()
            ).thenMany(
                mono {
                    if (user.authorities.isNotEmpty()) {
                        userAuthorityRepository.deleteAllByUserIdAndAuthorityIdNotIn(
                            user.id,
                            user.authorities.map { it.id }
                        )
                    } else {
                        userAuthorityRepository.deleteAllByUserId(user.id)
                    }
                }
            ).`as`(operator::transactional).asFlow().collect {}
    }

    override suspend fun getAuthorities(authorityIds: List<String>): List<Authority> {
        return authorityRepository.findAllById(authorityIds).map { authority ->
            authority.toModel() }.toList()
    }

    override suspend fun get(id: String): UserAggregate =
            userRepository.findById(id)?.run { toModel(getAuthorities(id)) } ?: throw UserNotFoundException()

    override suspend fun delete(id: String) {
        val entity = userRepository.findById(id) ?: throw UserNotFoundException()
        userRepository.delete(entity)
    }

    override suspend fun getByLogin(login: String): UserAggregate? {
        return withContext(Dispatchers.IO) {
            userRepository.findByLogin(login)
                ?.run {
                    toModel(getAuthorities(id))
                }
        }
    }

    override suspend fun getByPhone(phone: String): UserAggregate? {
        return withContext(Dispatchers.IO) {
            userRepository.findByPhone(phone)
                ?.run {
                    toModel(getAuthorities(id))
                }
        }
    }

    override suspend fun getByEmail(email: String): UserAggregate? {
        return withContext(Dispatchers.IO) {
            userRepository.findByEmail(email)
                ?.run {
                    toModel(getAuthorities(id))
                }
        }
    }

    override suspend fun existsWithLogin(login: String?): Boolean {
        return if (login != null) {
            val user = userRepository.findByLogin(login)
            return (user != null)
        } else {
            false
        }
    }

    override suspend fun existsWithEmail(email: String?): Boolean {
        return if (email != null) {
            val user = userRepository.findByEmail(email)
            return user != null
        } else {
            false
        }
    }

    override suspend fun existsWithPhone(phone: String?): Boolean {
        return if (phone != null) {
            val user = userRepository.findByPhone(phone)
            user != null
        } else {
            false
        }
    }

    override suspend fun checkAdminPrivilegesBySession(sessionUser: SessionUser) {
        val user = getByLogin(sessionUser.login) ?: throw UserNotFoundException()
        if (!user.checkAdminAuthority()) {
            throw AdminAuthorityNotFoundException()
        }
    }

    override suspend fun getAll(email: String?, page: Int, size: Int): Map<String, Any> {
            val offset = page * size
            val users = userRepository.findUsersByEmailWithPagination(email, size, offset).map { it.toModel(getAuthorities(it.id)) }.toList()
            val totalElements = userRepository.countUsersByEmail(email) // Получаем общее количество

            val totalPages = (totalElements + size - 1) / size // Вычисляем общее количество страниц
            return mapOf(
                "content" to users,
                "totalPages" to totalPages,
                "totalElements" to totalElements,
                "currentPage" to page // Возвращаем номер страницы, переданный в функцию
            )
    }


    private suspend fun getAuthorities(userId: String) =
            withContext(Dispatchers.IO) {
                userAuthorityRepository.findAllByUserId(userId)
                    .run {
                        authorityRepository.findAllById(map { it.authorityId }).map { authority ->
                            authority.toModel(firstOrNull { it.authorityId == authority.id }?.version)
                        }
                    }.toList()
            }


}