package com.dev.course.feature.authorization.domain.usecases

import com.dev.course.feature.authorization.domain.services.FirebaseAuthService
import com.dev.course.feature.authorization.presentation.dto.AuthResponseDto
import org.springframework.stereotype.Service
import java.util.*

interface AuthUseCase {
    suspend operator fun invoke(encodedToken: String) : AuthResponseDto
}

@Service
internal class AuthUseCaseImpl(
    private val service: FirebaseAuthService
) : AuthUseCase {
    override suspend fun invoke(encodedToken: String) : AuthResponseDto {
        val decodedBytes = Base64.getDecoder().decode(encodedToken)
        val decodedToken = String(decodedBytes)
        val credentials = decodedToken.split(":")
        if (credentials.size != 2) {
            throw IllegalArgumentException("Invalid credentials!")
        }
        val email = credentials.first()
        val password = credentials.last()
        println(email)
        println(password)
        return service.auth(email, password)
    }
}