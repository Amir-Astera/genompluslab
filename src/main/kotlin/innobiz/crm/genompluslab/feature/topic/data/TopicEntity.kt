package innobiz.crm.genompluslab.feature.topic.data

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "topic")
class TopicEntity(
        @Id
        val id: String,
        val name: String,
        @Version
        var version: Long?,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime
)