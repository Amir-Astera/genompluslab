package innobiz.crm.genompluslab.feature.topic.domain.services

import innobiz.crm.genompluslab.core.extension.toEntity
import innobiz.crm.genompluslab.core.extension.toModel
import innobiz.crm.genompluslab.feature.repositories.TopicRepository
import innobiz.crm.genompluslab.feature.topic.data.TopicEntity
import innobiz.crm.genompluslab.feature.topic.domain.errors.TopicNotFoundException
import innobiz.crm.genompluslab.feature.topic.domain.models.Topic
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

interface TopicService {
    suspend fun save(topic: Topic): String
    suspend fun get(id: String): Topic
    suspend fun delete(id: String)
    suspend fun getAll(): Collection<Topic>
}

@Service
internal class TopicServiceImpl(
    private val topicRepository: TopicRepository
): TopicService {
    override suspend fun save(topic: Topic): String = topicRepository.save(topic.toEntity()).id

    override suspend fun get(id: String): Topic =
        topicRepository.findById(id)?.toModel() ?: throw TopicNotFoundException(id)

    override suspend fun delete(id: String) = topicRepository.deleteById(id)

    override suspend fun getAll(): Collection<Topic> {
        return topicRepository.findAll().map { it.toModel() }.toList()
    }

}