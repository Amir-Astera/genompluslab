package innobiz.crm.genompluslab.feature.authorization.presentation.rest

import innobiz.crm.genompluslab.core.config.api.Controller
import innobiz.crm.genompluslab.feature.authorization.domain.usecases.AuthUseCase
import innobiz.crm.genompluslab.feature.authorization.presentation.dto.AuthResponseDto
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Parameter
import org.slf4j.Logger
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@Hidden
@RestController
class AuthController(
    logger: Logger,
    private val authUseCase: AuthUseCase
) : Controller(logger) {
    @PostMapping("/auth")
    suspend fun create(
        @Parameter(hidden = true)
        request: ServerHttpRequest
    ): ResponseEntity<AuthResponseDto> {
        val authorizationHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: ""
        val encodedToken = authorizationHeader.split(' ').lastOrNull() ?: ""
        val response = authUseCase(encodedToken)
        return ResponseEntity
            .status(HttpStatus.OK)
            .cacheControl(CacheControl.noCache())
            .body(response)
    }
}