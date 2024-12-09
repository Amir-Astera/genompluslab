package innobiz.crm.genompluslab.feature.topic.domain.usecases

import com.dev.course.core.config.api.CreateResponseDto
import innobiz.crm.genompluslab.feature.topic.domain.services.TopicService
import innobiz.crm.genompluslab.feature.topic.presentation.dto.UpdateTopicDto
import org.springframework.stereotype.Service

interface UpdateTopicUseCase {
    suspend operator fun invoke(dto: UpdateTopicDto): CreateResponseDto
}

@Service
internal class UpdateTopicUseCaseImpl(
    private val topicService: TopicService
): UpdateTopicUseCase {
    override suspend fun invoke(dto: UpdateTopicDto): CreateResponseDto {
        val topic = topicService.get(dto.id)
        val id = topicService.save(
                topic.copy(
                name = dto.name
        ))
        return CreateResponseDto(id)
    }

}



