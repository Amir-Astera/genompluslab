package innobiz.crm.genompluslab.feature.analytics.data

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "analytics_popular_topics")
class AnalyticsTopicsEntity(
        @Id
        val id: String,
        val topicId: String,
        val popularScore: Double,
        @Version
        var version: Long?,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime
)