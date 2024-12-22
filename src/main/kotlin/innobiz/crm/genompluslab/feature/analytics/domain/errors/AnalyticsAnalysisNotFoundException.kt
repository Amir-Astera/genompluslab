package innobiz.crm.genompluslab.feature.analytics.domain.errors

class AnalyticsAnalysisNotFoundException(
        val cityId: String,
        val analysisId: String
): RuntimeException("Analytics data not found for analysisId=$analysisId and cityId=$cityId!")