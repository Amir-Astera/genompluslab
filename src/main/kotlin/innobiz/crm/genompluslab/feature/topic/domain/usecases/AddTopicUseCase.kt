package innobiz.crm.genompluslab.feature.topic.domain.usecases

import com.dev.course.core.config.api.CreateResponseDto
import innobiz.crm.genompluslab.feature.topic.domain.models.Topic
import innobiz.crm.genompluslab.feature.topic.domain.services.TopicService
import innobiz.crm.genompluslab.feature.topic.presentation.dto.AddTopicDto
import org.springframework.stereotype.Service

interface AddTopicUseCase {
    suspend operator fun invoke(dto: AddTopicDto): CreateResponseDto
}

@Service
internal class AddTopicUseCaseImpl(
    private val topicService: TopicService
): AddTopicUseCase {
    override suspend fun invoke(dto: AddTopicDto): CreateResponseDto {
        //TODO сделать проверку на названия раздела
        val id = topicService.save(Topic(name = dto.name))
        return CreateResponseDto(id)
    }

}