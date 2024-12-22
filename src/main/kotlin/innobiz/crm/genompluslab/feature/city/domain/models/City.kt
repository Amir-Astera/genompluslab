package innobiz.crm.genompluslab.feature.city.domain.models

import java.util.UUID

data class City(
        val id: String = UUID.randomUUID().toString(),
        val name: String
)
