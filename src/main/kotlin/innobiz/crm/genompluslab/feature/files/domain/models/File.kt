package innobiz.crm.genompluslab.feature.files.domain.models


data class File(
        val id : String,
        val directory : FileDirectory,
        val format: String,
        val url : String,
        val version: Long? = null
)
