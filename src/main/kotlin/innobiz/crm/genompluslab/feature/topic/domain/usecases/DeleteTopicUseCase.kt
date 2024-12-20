package innobiz.crm.genompluslab.feature.topic.domain.usecases

import innobiz.crm.genompluslab.feature.topic.domain.services.TopicService
import innobiz.crm.genompluslab.feature.topic.presentation.dto.TopicDto
import org.springframework.stereotype.Service

interface DeleteTopicUseCase {
    suspend operator fun invoke(id: String)
}

@Service
internal class DeleteTopicUseCaseImpl(
        private val topicService: TopicService
): DeleteTopicUseCase {
    override suspend fun invoke(id: String) {
        val topic = topicService.get(id)
        topicService.delete(topic.id)
    }

}