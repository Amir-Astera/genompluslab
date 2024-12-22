package innobiz.crm.genompluslab.feature.analytics.domain.models

import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis

data class AnalysisWithPopularity(
        val analysis: Analysis,
        val popularScore: Double
)
