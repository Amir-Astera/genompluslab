package innobiz.crm.genompluslab.feature.analytics.domain.models

import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis

data class AnalysisWithScore(
        val analysis: Analysis,
        val popularScore: Double
)
