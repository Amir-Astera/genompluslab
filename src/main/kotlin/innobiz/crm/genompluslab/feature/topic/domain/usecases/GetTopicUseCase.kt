package innobiz.crm.genompluslab.feature.topic.domain.usecases

import innobiz.crm.genompluslab.feature.topic.domain.models.Topic
import innobiz.crm.genompluslab.feature.topic.domain.services.TopicService
import innobiz.crm.genompluslab.feature.topic.presentation.dto.TopicDto
import org.springframework.stereotype.Service

interface GetTopicUseCase {
    suspend operator fun invoke(dto: TopicDto): Topic
}

@Service
internal class GetTopicUseCaseImpl(
        private val topicService: TopicService
): GetTopicUseCase {
    override suspend fun invoke(dto: TopicDto): Topic {
        return topicService.get(dto.id)
    }

}