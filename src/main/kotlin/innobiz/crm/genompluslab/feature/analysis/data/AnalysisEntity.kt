package innobiz.crm.genompluslab.feature.analysis.data

import innobiz.crm.genompluslab.core.config.enums.Deadline
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "analysis")
class AnalysisEntity(
        @Id
        val id: String,
        val code: String,
        val name: String,
        val material: String,
        val deadline: String,
        val price: String,
        @Version
        var version: Long?,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime
)