package innobiz.crm.genompluslab.feature.authorization.presentation.dto

data class AuthFirebaseResponseDto(
    val localId: String,
    val email: String,
    val displayName: String,
    val idToken: String,
    val registered: Boolean,
    val refreshToken: String,
    val expiresIn: String
)