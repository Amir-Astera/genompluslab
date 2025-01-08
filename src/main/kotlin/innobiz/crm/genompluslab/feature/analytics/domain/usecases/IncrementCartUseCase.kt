package innobiz.crm.genompluslab.feature.analytics.domain.usecases

import innobiz.crm.genompluslab.feature.analytics.domain.services.AnalyticsService
import innobiz.crm.genompluslab.feature.analytics.presentation.dto.IncrementAnalyticsDto
import org.springframework.stereotype.Service

interface IncrementCartUseCase {
    suspend operator fun invoke(dto: IncrementAnalyticsDto)
}

@Service
internal class IncrementCartUseCaseImpl(
        private val analyticsService: AnalyticsService
): IncrementCartUseCase{
    override suspend fun invoke(dto: IncrementAnalyticsDto) {
        analyticsService.incrementCartCount(dto.cityId, dto.analysisId)
    }
}