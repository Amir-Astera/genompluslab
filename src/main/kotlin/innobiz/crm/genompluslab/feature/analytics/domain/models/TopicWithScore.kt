package innobiz.crm.genompluslab.feature.analytics.domain.models

import innobiz.crm.genompluslab.feature.topic.domain.models.Topic

data class TopicWithScore(
        val topicId: Topic,
        val popularScore: Double
)
