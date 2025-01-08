package innobiz.crm.genompluslab.feature.analytics.domain.services

import innobiz.crm.genompluslab.core.extension.toEntity
import innobiz.crm.genompluslab.core.extension.toModel
import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
import innobiz.crm.genompluslab.feature.analysis.domain.services.AnalysisService
import innobiz.crm.genompluslab.feature.analytics.data.AnalyticsAnalysisEntity
import innobiz.crm.genompluslab.feature.analytics.data.AnalyticsTopicsEntity
import innobiz.crm.genompluslab.feature.analytics.domain.errors.AnalyticsAnalysisNotFoundException
import innobiz.crm.genompluslab.feature.analytics.domain.models.AnalyticsAnalysis
import innobiz.crm.genompluslab.feature.analytics.domain.models.AnalyticsTopics
import innobiz.crm.genompluslab.feature.repositories.AnalyticsAnalysisRepository
import innobiz.crm.genompluslab.feature.repositories.AnalyticsTopicRepository
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

interface AnalyticsService{
    suspend fun saveAnalyticAnalysis(analyticsAnalysis: AnalyticsAnalysis)
    suspend fun incrementViewCount(cityId: String, analysisId: String)
    suspend fun incrementCartCount(cityId: String, analysisId: String)
    suspend fun incrementSalesCount(cityId: String, analysisId: String)
}
@Service
internal class AnalyticsServiceImpl(
    private val analyticsAnalysisRepository: AnalyticsAnalysisRepository,
): AnalyticsService {
    override suspend fun saveAnalyticAnalysis(analyticsAnalysis: AnalyticsAnalysis) {
        analyticsAnalysisRepository.save(analyticsAnalysis.toEntity())
    }

    @Transactional
    override suspend fun incrementViewCount(cityId: String, analysisId: String) {
        val analysis = analyticsAnalysisRepository.findAndLockAnalysisForUpdate(analysisId, cityId)?.toModel()
                ?: throw AnalyticsAnalysisNotFoundException(cityId, analysisId)
        analyticsAnalysisRepository.save(analysis.copy(viewsCount = analysis.viewsCount.inc()).toEntity())
    }

    @Transactional
    override suspend fun incrementCartCount(cityId: String, analysisId: String) {
        val analysis = analyticsAnalysisRepository.findAndLockAnalysisForUpdate(analysisId, cityId)?.toModel()
                ?: throw AnalyticsAnalysisNotFoundException(cityId, analysisId)
        analyticsAnalysisRepository.save(analysis.copy(cartCount = analysis.cartCount.inc()).toEntity())
    }

    @Transactional
    override suspend fun incrementSalesCount(cityId: String, analysisId: String) {
        val analysis = analyticsAnalysisRepository.findAndLockAnalysisForUpdate(analysisId, cityId)?.toModel()
                ?: throw AnalyticsAnalysisNotFoundException(cityId, analysisId)
        analyticsAnalysisRepository.save(analysis.copy(salesCount = analysis.salesCount.inc()).toEntity())
    }
}