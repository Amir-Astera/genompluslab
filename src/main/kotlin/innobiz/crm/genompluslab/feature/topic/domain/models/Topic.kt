package innobiz.crm.genompluslab.feature.topic.domain.models

import java.time.LocalDateTime
import java.util.*

data class Topic(
        val id: String = UUID.randomUUID().toString(),
        val name: String,
        val version: Long? = null
)
