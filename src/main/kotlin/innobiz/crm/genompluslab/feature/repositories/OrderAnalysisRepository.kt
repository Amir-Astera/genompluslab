package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.feature.order.data.OrderAnalysisEntity
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderAnalysisRepository: CoroutineCrudRepository<OrderAnalysisEntity, String> {
    suspend fun findAllByOrderId(orderId: String): Flow<OrderAnalysisEntity>
}