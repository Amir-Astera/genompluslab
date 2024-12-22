package innobiz.crm.genompluslab.feature.analytics.domain.models

import java.util.UUID

data class AnalyticsTopics(
        val id: String = UUID.randomUUID().toString(),
        val topicId: String,
        val cityId: String,
        val popularScore: Double = 0.0,
        val version: Long? = null
)
