package innobiz.crm.genompluslab.feature.analytics.domain.models

import innobiz.crm.genompluslab.feature.topic.domain.models.Topic

data class TopicWithScore(
        val topic: Topic,
        val totalPopularity: Double,
        val minPrice: Double,
        val totalAnalyses: Int
)
