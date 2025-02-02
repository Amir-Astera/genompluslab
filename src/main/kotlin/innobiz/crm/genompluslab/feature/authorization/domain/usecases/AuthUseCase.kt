package innobiz.crm.genompluslab.feature.authorization.domain.usecases

import innobiz.crm.genompluslab.feature.authorization.domain.services.FirebaseAuthService
import innobiz.crm.genompluslab.feature.authorization.presentation.dto.AuthResponseDto
import org.springframework.http.ResponseCookie
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

interface AuthUseCase {
    suspend operator fun invoke(encodedToken: String, response: ServerHttpResponse)
}

@Service
internal class AuthUseCaseImpl(
    private val service: FirebaseAuthService
) : AuthUseCase {
    override suspend fun invoke(encodedToken: String, response: ServerHttpResponse) {
        val decodedBytes = Base64.getDecoder().decode(encodedToken)
        val decodedToken = String(decodedBytes)
        val credentials = decodedToken.split(":")
        if (credentials.size != 2) {
            throw IllegalArgumentException("Invalid credentials!")
        }
        val email = credentials.first()
        val password = credentials.last()
        val token = service.auth(email, password)

        val cookie = ResponseCookie.from("SESSIONID", token.accessToken)
                .httpOnly(true)
                .secure(true) // true, если HTTPS
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite("None") // или "Lax"
                .build()

        response.addCookie(cookie)
    }
}