package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.feature.authority.data.entity.AuthorityEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorityRepository : CoroutineCrudRepository<AuthorityEntity, String> {
    suspend fun findByName(name: String): AuthorityEntity?
}
