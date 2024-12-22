package innobiz.crm.genompluslab.feature.cart.domain.services

import com.fasterxml.jackson.core.PrettyPrinter
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

        println(analyses.map { it })

        val summary = withContext(Dispatchers.IO) {
            return@withContext databaseClient.sql("""
            SELECT COUNT(c.analysis_id) AS totalCount, 
                   SUM(a.price) AS totalSum, 
                   c.user_id AS userId
            FROM cart c
            JOIN analysis a ON c.analysis_id = a.id
            WHERE c.user_id = :userId
            GROUP BY c.user_id
        """)
                    .bind("userId", userId)
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

}