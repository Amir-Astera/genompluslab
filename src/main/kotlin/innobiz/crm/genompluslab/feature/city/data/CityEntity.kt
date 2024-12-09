package innobiz.crm.genompluslab.feature.city.data

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "city")
class CityEntity(
        @Id
        val id: String,
        val name: String,
        val userId: String,
        val analysisId: String,
        @Version
        val version: Long?,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime
)