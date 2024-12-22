package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.feature.users.data.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: CoroutineCrudRepository<UserEntity, String> {
    suspend fun findByLogin(login: String): UserEntity?

    suspend fun findByEmail(email: String): UserEntity?

    suspend fun findByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE (:email IS NULL OR email LIKE :email) LIMIT :limit OFFSET :offset")
    suspend fun findUsersByEmailWithPagination(email: String?, limit: Int, offset: Int): Flow<UserEntity>

    @Query("SELECT COUNT(*) FROM users WHERE (:email IS NULL OR email LIKE :email)")
     suspend fun countUsersByEmail(email: String?): Long
}

//        // @Query("SELECT * FROM users WHERE (:email IS NULL OR email LIKE :email) LIMIT :limit OFFSET :offset")
//        //    fun findUsersByEmailWithPagination(email: String?, limit: Int, offset: Int): Flow<User>
//        //
//        //    @Query("SELECT COUNT(*) FROM users WHERE (:email IS NULL OR email LIKE :email)")
//        //    suspend fun countUsersByEmail(email: String?): Long