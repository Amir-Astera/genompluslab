package innobiz.crm.genompluslab.feature.topic.domain.usecases

import innobiz.crm.genompluslab.feature.topic.domain.services.TopicService
import org.springframework.boot.autoconfigure.kafka.KafkaProperties.Retry.Topic
import org.springframework.stereotype.Service

interface GetAllTopicUseCase {
    suspend operator fun invoke(cityId: String, page: Int, size: Int): Map<String, Any>
}

@Service
internal class GetAllTopicUseCaseImpl(
        private val topicService: TopicService
): GetAllTopicUseCase {
    override suspend fun invoke(cityId: String, page: Int, size: Int): Map<String, Any> {
        return topicService.getAll(cityId, page, size)
    }

}