package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.feature.authority.data.entity.UserAuthorityEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAuthorityRepository : CoroutineCrudRepository<UserAuthorityEntity, String> {
    fun findAllByUserId(userId: String): Flow<UserAuthorityEntity>

    @Query("delete from user_authority v where v.user_id = :userId and v.authority_id not in (:authorityIds)")
    suspend fun deleteAllByUserIdAndAuthorityIdNotIn(userId: String, authorityIds: List<String>)

    @Query("delete from user_authority v where v.user_id = :userId")
    suspend fun deleteAllByUserId(userId: String)
}