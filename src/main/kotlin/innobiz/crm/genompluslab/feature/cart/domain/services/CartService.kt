package innobiz.crm.genompluslab.feature.cart.domain.services

import com.fasterxml.jackson.core.PrettyPrinter
import innobiz.crm.genompluslab.core.config.enums.CartStatus
import innobiz.crm.genompluslab.core.extension.toEntity
import innobiz.crm.genompluslab.core.extension.toModel
import innobiz.crm.genompluslab.feature.analysis.domain.errors.AnalysisNotFoundException
import innobiz.crm.genompluslab.feature.cart.domain.errors.CartNotFoundException
import innobiz.crm.genompluslab.feature.cart.domain.models.Cart
import innobiz.crm.genompluslab.feature.cart.domain.models.CartDetailsDto
import innobiz.crm.genompluslab.feature.cart.presentation.dto.GetCartDto
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface CartService {
    suspend fun save(cart: Cart)
    suspend fun get(userId: String, status: CartStatus): GetCartDto?
    suspend fun deleteAnalysis(userId: String, analysisId: String)
    suspend fun changeStatusToPaid(userId: String, analyses: Collection<String>)
    suspend fun changeStatusToWaiting(userId: String, analyses: Collection<String>)
    suspend fun changeStatusToIn(userId: String, analyses: Collection<String>)
    suspend fun changeStatusToOrdered(userId: String, analyses: Collection<String>)
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

    override suspend fun get(userId: String, status: CartStatus): GetCartDto? {
        val cart = cartRepository.findByUserIdAndStatus(userId, status)?.toList()
        if (cart.isNullOrEmpty()) return GetCartDto(0.0, 0, emptyList())

        val analyses = cart.run {
            map {
                analysisRepository.findById(it.analysisId)?.toModel() ?: throw AnalysisNotFoundException(it.analysisId)
            }
        }

        return GetCartDto(
                totalSum = analyses.sumOf { it.price },
                totalCount = analyses.size,
                analyses = analyses
        )
    }

    override suspend fun deleteAnalysis(userId: String, analysisId: String) {
        val cart = cartRepository.findByUserIdAndAnalysisId(userId, analysisId) ?: throw CartNotFoundException(userId)
        cartRepository.deleteById(cart.id)
    }

    override suspend fun changeStatusToPaid(userId: String, analyses: Collection<String>) {
        //TODO обернуть в try catch и обрабатывать ошибки когда не нашлось по статусу.
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

    override suspend fun changeStatusToOrdered(userId: String, analyses: Collection<String>) {
        analyses.map {
            val cart = cartRepository.findByUserIdAndStatusAndAnalysisId(userId, CartStatus.PAID, it).awaitSingle()
                    .toModel()
                    .copy(status = CartStatus.ORDERED)
            cartRepository.save(cart.toEntity())
        }
    }
}