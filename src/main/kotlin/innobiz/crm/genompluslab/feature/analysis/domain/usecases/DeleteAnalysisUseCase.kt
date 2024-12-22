package innobiz.crm.genompluslab.feature.analysis.domain.usecases

import innobiz.crm.genompluslab.feature.analysis.domain.services.AnalysisService
import org.springframework.stereotype.Service

interface DeleteAnalysisUseCase {
    suspend operator fun invoke(id: String)
}
@Service
class DeleteAnalysisUseCaseImpl(
        private val analysisService: AnalysisService
): DeleteAnalysisUseCase {
    override suspend fun invoke(id: String) {
        analysisService.delete(id)
    }

}