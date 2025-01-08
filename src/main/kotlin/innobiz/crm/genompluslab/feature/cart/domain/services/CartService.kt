package innobiz.crm.genompluslab.feature.cart.domain.services

import com.fasterxml.jackson.core.PrettyPrinter
import innobiz.crm.genompluslab.core.config.enums.CartStatus
import innobiz.crm.genompluslab.core.extension.toEntity
import innobiz.crm.genompluslab.core.extension.toModel
import innobiz.crm.genompluslab.feature.analysis.domain.errors.AnalysisNotFoundException
import innobiz.crm.genompluslab.feature.cart.domain.errors.CartNotFoundException
import innobiz.crm.genompluslab.feature.cart.domain.models.Cart
import innobiz.crm.genompluslab.feature.cart.domain.models.CartDetailsDto
import innobiz.crm.genompluslab.feature.repositories.AnalysisRepository
import innobiz.crm.genompluslab.feature.repositories.CartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service

interface CartService {
    suspend fun save(cart: Cart)
    suspend fun get(userId: String): Map<String, Any>
    suspend fun deleteAnalysis(userId: String, analysisId: String)
    suspend fun changeStatusToPaid(userId: String, analyses: Collection<String>)
    suspend fun changeStatusToWaiting(userId: String, analyses: Collection<String>)
    suspend fun changeStatusToIn(userId: String, analyses: Collection<String>)
}

@Service
internal class CartServiceImpl(
        private val cartRepository: CartRepository,
        private val analysisRepository: AnalysisRepository,
        private val databaseClient: DatabaseClient
): CartService {
    override suspend fun save(cart: Cart) {
        cartRepository.save(cart.toEntity())
    }

    override suspend fun get(userId: String): Map<String, Any> {
        val analyses = cartRepository.findByUserId(userId)?.run {
            map {
                analysisRepository.findById(it.analysisId)?.toModel() ?: throw AnalysisNotFoundException(it.analysisId)
            }
        } ?: throw CartNotFoundException(userId)

        val summary = withContext(Dispatchers.IO) {
            return@withContext databaseClient.sql("""
            SELECT COUNT(c.analysis_id) AS totalCount, 
                   SUM(a.price) AS totalSum, 
                   c.user_id AS userId
            FROM cart c
            JOIN analysis a ON c.analysis_id = a.id
            WHERE c.user_id = :userId
              AND c.status = :status
            GROUP BY c.user_id
            """)
                    .bind("userId", userId)
                    .bind("status", CartStatus.PAID.name)
                    .map { row, _ ->
                        CartDetailsDto(
                                totalCount = row.get("totalCount", java.lang.Long::class.java)?.toLong() ?: 0L,
                                totalSum = row.get("totalSum", java.lang.Double::class.java)?.toDouble() ?: 0.0,
                                userId = row.get("userId", String::class.java) ?: throw IllegalStateException("User ID not found")
                        )
                    }
                    .one()
                    .awaitSingle()
        }



        return mapOf(
                "totalSum" to summary.totalSum,
                "totalCount" to summary.totalCount,
                "analyses" to analyses.toList()
        )
    }

    override suspend fun deleteAnalysis(userId: String, analysisId: String) {
        val cart = cartRepository.findByUserIdAndAnalysisId(userId, analysisId) ?: throw CartNotFoundException(userId)
        cartRepository.deleteById(cart.id)
    }

    override suspend fun changeStatusToPaid(userId: String, analyses: Collection<String>) {
        analyses.map {
            val cart = cartRepository.findByUserIdAndStatusAndAnalysisId(userId, CartStatus.WAITING, it).awaitSingle()
                    .toModel()
                    .copy(status = CartStatus.PAID)
            cartRepository.save(cart.toEntity())
        }
    }

    override suspend fun changeStatusToWaiting(userId: String, analyses: Collection<String>) {
        analyses.map {
            val cart = cartRepository.findByUserIdAndStatusAndAnalysisId(userId, CartStatus.IN, it).awaitSingle()
                    .toModel()
                    .copy(status = CartStatus.WAITING)
            cartRepository.save(cart.toEntity())
        }
    }

    override suspend fun changeStatusToIn(userId: String, analyses: Collection<String>) {
        analyses.map {
            val cart = cartRepository.findByUserIdAndStatusAndAnalysisId(userId, CartStatus.WAITING, it).awaitSingle()
                    .toModel()
                    .copy(status = CartStatus.IN)
            cartRepository.save(cart.toEntity())
        }
    }
}