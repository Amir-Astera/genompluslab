package innobiz.crm.genompluslab.feature.topic.domain.usecases

import innobiz.crm.genompluslab.feature.analytics.domain.models.TopicWithScore
import innobiz.crm.genompluslab.feature.topic.domain.models.Topic
import innobiz.crm.genompluslab.feature.topic.domain.services.TopicService
import org.springframework.stereotype.Service

interface GetTopTopicsUseCase {
    suspend operator fun invoke(cityId: String): Collection<TopicWithScore>
}

@Service
internal class GetTopTopicsUseCaseImpl(
    private val topicService: TopicService
): GetTopTopicsUseCase {
    override suspend fun invoke(cityId: String): Collection<TopicWithScore> {
        return topicService.getTopTopics(cityId)
    }

}