package innobiz.crm.genompluslab.feature.city.data

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table(name = "city")
data class CityEntity(
        @Id
        val id: String = UUID.randomUUID().toString(),
        val name: String,
        @Version
        var version: Long?,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
)