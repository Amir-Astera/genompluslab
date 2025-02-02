package innobiz.crm.genompluslab.feature.authorization.presentation.rest

import com.google.api.client.auth.oauth2.TokenRequest
import com.sun.security.auth.UserPrincipal
import innobiz.crm.genompluslab.core.config.api.Controller
import innobiz.crm.genompluslab.core.security.SessionUser
import innobiz.crm.genompluslab.feature.authorization.domain.usecases.AuthUseCase
import innobiz.crm.genompluslab.feature.authorization.domain.usecases.CurrentSessionUseCase
import innobiz.crm.genompluslab.feature.authorization.domain.usecases.LogoutUseCase
import innobiz.crm.genompluslab.feature.authorization.domain.usecases.StoreTokenUseCase
import innobiz.crm.genompluslab.feature.authorization.presentation.dto.AuthResponseDto
import innobiz.crm.genompluslab.feature.authorization.presentation.dto.StoreToken
import innobiz.crm.genompluslab.feature.authorization.presentation.dto.UserInfo
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.reactive.awaitFirst
import org.apache.http.auth.UsernamePasswordCredentials
import org.slf4j.Logger
import org.springframework.boot.actuate.web.exchanges.HttpExchange.Principal
import org.springframework.http.CacheControl
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty


@Hidden
@RestController
class AuthController(
    logger: Logger,
    private val authUseCase: AuthUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val storeTokenUseCase: StoreTokenUseCase,
    private val currentSessionUseCase: CurrentSessionUseCase
) : Controller(logger) {
    @PostMapping("/auth")
    suspend fun create(
        @Parameter(hidden = true)
        request: ServerHttpRequest,
        @Parameter(hidden = true)
        response: ServerHttpResponse
    ): ResponseEntity<Any> {
        val authorizationHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: ""
        val encodedToken = authorizationHeader.split(' ').lastOrNull() ?: ""
        authUseCase(encodedToken, response)
        return ResponseEntity.ok().body(mapOf("status" to "ok"))
    }

    @SecurityRequirement(name = "security_auth")
    @PostMapping("/logout")
    suspend fun logout(
            @Parameter(hidden = true)
            request: ServerHttpRequest,
            @Parameter(hidden = true)
            response: ServerHttpResponse
    ): ResponseEntity<Any> {
        logoutUseCase(response)
        return ResponseEntity
                .status(HttpStatus.OK)
                .cacheControl(CacheControl.noCache())
                .body(response)
    }

//    @SecurityRequirement(name = "security_auth")
    @PostMapping("/auth/store")
    suspend fun storeToken(
            @RequestBody token: StoreToken,
            @Parameter(hidden = true)
            request: ServerHttpRequest,
            @Parameter(hidden = true)
            response: ServerHttpResponse
    ): ResponseEntity<Any> {
        storeTokenUseCase(token, response)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/auth/me")
    suspend fun currentSession(
            @Parameter(hidden = true)
            exchange: ServerWebExchange
    ): ResponseEntity<UserInfo> {
        return ResponseEntity.ok(currentSessionUseCase(exchange).awaitFirst())
    }
}