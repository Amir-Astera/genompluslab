package innobiz.crm.genompluslab.feature.analysis.domain.usecases

import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
import innobiz.crm.genompluslab.feature.analysis.domain.services.AnalysisService
import innobiz.crm.genompluslab.feature.analytics.domain.models.AnalysisWithPopularity
import innobiz.crm.genompluslab.feature.analytics.domain.models.AnalysisWithScore
import org.springframework.stereotype.Service

interface GetTopAnalysesUseCase {
    suspend operator fun invoke(cityId: String): Collection<AnalysisWithPopularity>
}

@Service
internal class GetTopAnalysesUseCaseImpl(
    private val analysisService: AnalysisService
): GetTopAnalysesUseCase {
    override suspend fun invoke(cityId: String): Collection<AnalysisWithPopularity> {
        return analysisService.getPopular(cityId)
    }

}