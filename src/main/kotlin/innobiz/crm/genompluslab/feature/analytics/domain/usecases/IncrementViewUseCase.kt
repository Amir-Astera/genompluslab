package innobiz.crm.genompluslab.feature.analytics.domain.usecases

import innobiz.crm.genompluslab.feature.analytics.domain.services.AnalyticsService
import innobiz.crm.genompluslab.feature.analytics.presentation.dto.IncrementAnalyticsDto
import org.springframework.stereotype.Service

interface IncrementViewUseCase {
    suspend operator fun invoke(dto: IncrementAnalyticsDto)
}

@Service
internal class IncrementViewUseCaseImpl(
        private val analyticsService: AnalyticsService
): IncrementViewUseCase {
    override suspend fun invoke(dto: IncrementAnalyticsDto) {
        analyticsService.incrementViewCount(dto.cityId, dto.analysisId)
    }

}