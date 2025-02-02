package innobiz.crm.genompluslab.feature.authorization.domain.services

import com.google.cloud.storage.Acl.User
import com.google.firebase.ErrorCode
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import innobiz.crm.genompluslab.core.config.properties.SecurityProperties
import innobiz.crm.genompluslab.core.extension.apiFutureToMono
import innobiz.crm.genompluslab.feature.authorization.domain.errors.FirebaseAuthException
import innobiz.crm.genompluslab.feature.authorization.presentation.dto.AuthFirebaseResponseDto
import innobiz.crm.genompluslab.feature.authorization.presentation.dto.AuthRequestDto
import innobiz.crm.genompluslab.feature.authorization.presentation.dto.AuthResponseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.withContext
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitEntity
import org.springframework.web.reactive.function.client.awaitExchange


interface FirebaseAuthService {
    suspend fun auth(email: String, password: String): AuthResponseDto
    suspend fun verifyToken(token: String): Boolean
}

@Service
class FirebaseAuthServiceImpl(
    webClientBuilder: WebClient.Builder,
    private val securityProperties: SecurityProperties,
    private val firebaseAuth: FirebaseAuth
) : FirebaseAuthService {
    private val webClient = webClientBuilder.build()

    override suspend fun auth(email: String, password: String): AuthResponseDto {
        val firebaseProps = securityProperties.firebaseProps
        val baseUrl = firebaseProps.apiIdentityUrl
        val url = "${baseUrl}:signInWithPassword?key=${firebaseProps.apiKey}"

        val requestData = AuthRequestDto(
            email = email,
            password = password,
            returnSecureToken = true
        )
        val response = webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestData)
                .awaitExchange { it.awaitEntity<AuthFirebaseResponseDto>() }
        val responseData = response.body
        if (response.statusCode != HttpStatus.OK || responseData == null) {
            throw FirebaseAuthException(FirebaseException(ErrorCode.UNAVAILABLE, "Authorization failed!", Throwable()).message ?: "EROrArrrrrrrrrrrrrrrrrrrrrr")
        }

        return AuthResponseDto(
            tokenType = "Bearer",
            accessToken = responseData.idToken,
            refreshToken = responseData.refreshToken,
            expiresIn = responseData.expiresIn
        )
    }

    override suspend fun verifyToken(token: String): Boolean {
        val firebaseToken = withContext(Dispatchers.IO) {
            apiFutureToMono(firebaseAuth.verifyIdTokenAsync(token))
                    .doOnError {
                        throw FirebaseAuthException("Token not valid!")
                    }
                    .awaitFirst()
        }
        return firebaseToken != null
    }
}