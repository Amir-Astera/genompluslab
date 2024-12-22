package innobiz.crm.genompluslab.feature.analysis.domain.usecases

import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
import innobiz.crm.genompluslab.feature.analysis.domain.services.AnalysisService
import innobiz.crm.genompluslab.feature.topic.domain.services.TopicService
import org.springframework.stereotype.Service

interface SearchAnalysisByNameUseCase {
    suspend operator fun invoke(cityId: String, text: String, page: Int, size: Int): Map<String, Any>
}

@Service
internal class SearchAnalysisByNameUseCaseImpl(
        private val topicService: TopicService,
        private val analysisService: AnalysisService
): SearchAnalysisByNameUseCase {
    override suspend fun invoke(cityId: String, text: String, page: Int, size: Int): Map<String, Any> {
        return if (topicService.isTopic(cityId, text) != 0L) {
                topicService.getByName(cityId, text, page, size)
        } else {
            analysisService.search(cityId, text, page, size)
        }
    }

}