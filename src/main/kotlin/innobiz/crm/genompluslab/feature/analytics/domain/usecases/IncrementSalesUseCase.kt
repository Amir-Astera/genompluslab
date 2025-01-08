package innobiz.crm.genompluslab.feature.analytics.domain.usecases

import innobiz.crm.genompluslab.feature.analytics.domain.services.AnalyticsService
import innobiz.crm.genompluslab.feature.analytics.presentation.dto.IncrementAnalyticsDto
import org.springframework.stereotype.Service

interface IncrementSalesUseCase {
    suspend operator fun invoke(dto: IncrementAnalyticsDto)
}

@Service
internal class IncrementSalesUseCaseImpl(
     private val analyticsService: AnalyticsService
): IncrementSalesUseCase {
    override suspend fun invoke(dto: IncrementAnalyticsDto) {
        analyticsService.incrementSalesCount(dto.cityId, dto.analysisId)
    }
}