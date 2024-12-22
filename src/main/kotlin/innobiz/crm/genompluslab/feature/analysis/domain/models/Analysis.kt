package innobiz.crm.genompluslab.feature.analysis.domain.models

import java.util.UUID

data class Analysis (
        val id: String = UUID.randomUUID().toString(),
        val code: String,
        val name: String,
        val material: String,
        val deadline: String,
        val price: Double,
        val description: String,
        var version: Long? = null,
)

//TODO сделать и тут и в entity подготовку к анализу и показания и интерпретация резульатов