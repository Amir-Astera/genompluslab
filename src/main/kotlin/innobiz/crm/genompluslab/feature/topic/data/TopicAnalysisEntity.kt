package innobiz.crm.genompluslab.feature.topic.data

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "topic_analysis")
class TopicAnalysisEntity(
        @Id
        val id: String,
        val topicId: String,
        val analysisId: String,
        @Version
        val version: Long?,
        val createdAt: LocalDateTime? = null
)