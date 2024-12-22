package innobiz.crm.genompluslab.feature.analysis.domain.usecases

import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
import innobiz.crm.genompluslab.feature.analysis.domain.services.AnalysisService
import org.springframework.stereotype.Service

interface GetAnalysisUseCase {
    suspend operator fun invoke(id: String): Analysis
}
@Service
class GetAnalysisUseCaseImpl(
        private val analysisService: AnalysisService
): GetAnalysisUseCase {
    override suspend fun invoke(id: String): Analysis {
        return analysisService.get(id)
    }

}