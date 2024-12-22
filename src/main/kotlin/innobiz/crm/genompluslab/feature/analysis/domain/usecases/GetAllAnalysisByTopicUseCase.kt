package innobiz.crm.genompluslab.feature.analysis.domain.usecases

import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
import innobiz.crm.genompluslab.feature.analysis.domain.services.AnalysisService
import innobiz.crm.genompluslab.feature.topic.domain.services.TopicService
import org.springframework.stereotype.Service

interface GetAllAnalysisByTopicUseCase {
    suspend operator fun invoke(cityId: String, topicId: String, page: Int, size: Int): Map<String, Any>
}
@Service
class GetAllAnalysisByTopicUseCaseImpl(
        private val analysisService: AnalysisService,
        private val topicService: TopicService
): GetAllAnalysisByTopicUseCase {
    override suspend fun invoke(cityId: String, topicId: String, page: Int, size: Int): Map<String, Any> {
        val topic = topicService.get(topicId)
        return analysisService.getAllByTopicAndCity(cityId, topic.id, page, size)
    }
}