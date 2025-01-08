package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.feature.order.data.OrderEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OrderRepository: CoroutineCrudRepository<OrderEntity, String> {
}