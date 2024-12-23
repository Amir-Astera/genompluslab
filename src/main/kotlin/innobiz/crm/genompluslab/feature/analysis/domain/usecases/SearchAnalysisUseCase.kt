package innobiz.crm.genompluslab.feature.analysis.domain.usecases

import innobiz.crm.genompluslab.feature.analysis.domain.services.AnalysisService
import org.springframework.stereotype.Service

interface SearchAnalysisUseCase {
    suspend operator fun invoke(city: String, name: String, page: Int, size: Int): Map<String, Any>
}

@Service
internal class SearchAnalysisUseCaseImpl(
        private val analysisService: AnalysisService
): SearchAnalysisUseCase {
    override suspend fun invoke(city: String, name: String, page: Int, size: Int): Map<String, Any> {
        return analysisService.search(city, name, page, size)
    }

}