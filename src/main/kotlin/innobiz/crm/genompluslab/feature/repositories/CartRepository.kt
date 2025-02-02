package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.core.config.enums.CartStatus
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

    suspend fun findByUserIdAndStatus(userId: String, status: CartStatus): Flow<CartEntity>?

    suspend fun findByUserIdAndAnalysisId(userId: String, analysisId: String): CartEntity?

    @Query("""
        SELECT * FROM cart 
        WHERE user_id = :userId 
          AND status = :status
          AND analysis_id = :analysisId
    """)
    fun findByUserIdAndStatusAndAnalysisId(
            userId: String,
            status: CartStatus,
            analysisId: String
    ): Mono<CartEntity>


}