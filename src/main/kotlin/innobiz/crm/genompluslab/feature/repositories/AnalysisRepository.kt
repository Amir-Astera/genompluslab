package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.feature.analysis.data.AnalysisEntity
import innobiz.crm.genompluslab.feature.analytics.domain.models.AnalysisWithPopularity
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface AnalysisRepository: CoroutineCrudRepository<AnalysisEntity, String> {
}