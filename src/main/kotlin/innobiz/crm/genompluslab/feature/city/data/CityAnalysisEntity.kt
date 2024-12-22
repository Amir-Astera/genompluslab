package innobiz.crm.genompluslab.feature.city.data

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime
import java.util.*

@Table(name = "city_analysis")
class CityAnalysisEntity(
        @Id
        val id: String = UUID.randomUUID().toString(),
        val cityId: String,
        val analysisId: String,
        @Version
        var version: Long? = null,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null
)