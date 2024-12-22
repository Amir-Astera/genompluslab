package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.feature.analysis.data.AnalysisEntity
import innobiz.crm.genompluslab.feature.cart.data.CartEntity
import innobiz.crm.genompluslab.feature.cart.domain.models.CartDetailsDto
import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.Flow
import org.postgresql.core.Tuple
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CartRepository: CoroutineCrudRepository<CartEntity, String> {
    suspend fun findByUserId(userId: String): Flow<CartEntity>?

    suspend fun findByUserIdAndAnalysisId(userId: String, analysisId: String): CartEntity?

    @Query("""
    SELECT COUNT(c.analysis_id) AS totalCount, 
           SUM(a.price) AS totalSum, 
           c.user_id AS userId
    FROM cart c
    JOIN analysis a ON c.analysis_id = a.id
    WHERE c.user_id = :userId
    GROUP BY c.user_id
""")
    suspend fun getCartSummary(userId: String): Map<String, Any>

}