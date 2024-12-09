package com.dev.course.feature.authorization.domain.services

import com.dev.course.core.config.properties.SecurityProperties
import com.dev.course.feature.authorization.presentation.dto.AuthFirebaseResponseDto
import com.dev.course.feature.authorization.presentation.dto.AuthRequestDto
import com.dev.course.feature.authorization.presentation.dto.AuthResponseDto
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitEntity
import org.springframework.web.reactive.function.client.awaitExchange

interface FirebaseAuthService {
    suspend fun auth(email: String, password: String): AuthResponseDto
}

@Service
class FirebaseAuthServiceImpl(
    webClientBuilder: WebClient.Builder,
    private val securityProperties: SecurityProperties
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
        println("--------------------------------------------------------")
        println("Gradle built")
        println("--------------------------------------------------------")

        val responseData = response.body
        println(responseData)



//        if (response.statusCode() != HttpStatus.OK) {
//            throw FirebaseAuthException("Authorization failed!")
//        }

//        println(response.statusCode())
//        println(response.awaitEntity<AuthFirebaseResponseDto>().body)
//        println(response.awaitEntity<AuthFirebaseResponseDto>().body?.email)
//
//        val responseData = response.awaitEntity<AuthFirebaseResponseDto>().body
//            ?: throw FirebaseAuthException("Authorization failed: Unrecognized response!")

        println(responseData!!.idToken)
        return AuthResponseDto(
            tokenType = "Bearer",
            accessToken = responseData!!.idToken,
            refreshToken = responseData.refreshToken,
            expiresIn = responseData.expiresIn
        )
    }
}
