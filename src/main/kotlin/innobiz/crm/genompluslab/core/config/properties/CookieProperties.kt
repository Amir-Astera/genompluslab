package innobiz.crm.genompluslab.core.config.properties

data class CookieProperties(
    val domain: String,
    val path: String,
    val httpOnly: Boolean,
    val secure: Boolean,
    val maxAgeInMinutes: Int
)