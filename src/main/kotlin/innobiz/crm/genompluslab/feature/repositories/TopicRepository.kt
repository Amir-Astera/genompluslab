package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.feature.topic.data.TopicEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TopicRepository:  CoroutineCrudRepository<TopicEntity, String>{
}