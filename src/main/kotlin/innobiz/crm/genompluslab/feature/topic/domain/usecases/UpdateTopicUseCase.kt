package innobiz.crm.genompluslab.feature.topic.domain.usecases

import innobiz.crm.genompluslab.feature.topic.domain.services.TopicService
import innobiz.crm.genompluslab.feature.topic.presentation.dto.TopicDto
import org.springframework.stereotype.Service

interface UpdateTopicUseCase {
    suspend operator fun invoke(id: String, dto: TopicDto)
}

@Service
internal class UpdateTopicUseCaseImpl(
    private val topicService: TopicService
): UpdateTopicUseCase {
    override suspend fun invoke(id: String, dto: TopicDto) {
        val topic = topicService.get(id)
        topicService.save(
                topic.copy(
                name = dto.name
        ))
    }

}



