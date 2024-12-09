package com.dev.course.core.config

import com.google.firebase.auth.FirebaseAuth
import com.dev.course.core.config.properties.SecurityProperties
import com.dev.course.core.security.TokenAuthenticationConverter
import com.dev.course.core.security.UnauthorizedAuthenticationEntryPoint
import com.dev.course.core.security.firebase.FirebaseHeadersExchangeMatcher
import com.dev.course.core.security.firebase.FirebaseTokenAuthenticationManager
import com.dev.course.feature.authorization.domain.usecases.SaveSessionUserUseCase
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer


@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
class SecurityConfig(
        private val securityProperties: SecurityProperties
) : WebFluxConfigurer {

    override fun addCorsMappings(corsRegistry: CorsRegistry) {
        corsRegistry.addMapping("/**")
            .allowedMethods("*")
                .allowedOrigins(*securityProperties.allowedOrigins.toTypedArray())
                .allowedHeaders(*securityProperties.allowedHeaders.toTypedArray())
                .allowedMethods(*securityProperties.allowedMethods.toTypedArray())
                .exposedHeaders(*securityProperties.exposedHeaders.toTypedArray())
//                .allowCredentials(true)
                .maxAge(3600)
    }

    @Bean
    fun springSecurityFilterChain(
            http: ServerHttpSecurity,
            entryPoint: UnauthorizedAuthenticationEntryPoint,
            authWebFilter: AuthenticationWebFilter
    ): SecurityWebFilterChain {

        http.csrf { it.disable() }
            .formLogin { it.disable() }
            .logout { it.disable() }
            .httpBasic { it.disable() }

        http.exceptionHandling { it.authenticationEntryPoint(entryPoint) }
            .authorizeExchange { it.pathMatchers(HttpMethod.OPTIONS).permitAll() }
            .authorizeExchange { it.pathMatchers(*securityProperties.allowedPublicApis.toTypedArray()).permitAll() }
            .authorizeExchange { it.pathMatchers("/", "https://backend.curs-clearfinance.kz/", "https://backend.curs-clearfinance.kz", "http://172.18.0.3:8080", "http://172.18.0.3:8080/", "/api/*", "/api/**", "http://localhost:3000/", "http://localhost:3000").permitAll() }
            .authorizeExchange { it.matchers(EndpointRequest.toAnyEndpoint()).authenticated() }
            .addFilterAt(authWebFilter, SecurityWebFiltersOrder.AUTHORIZATION)
            .authorizeExchange { it.anyExchange().authenticated() }

        return http.build()
    }

    @Bean
    fun authWebFilter(authenticationManager: ReactiveAuthenticationManager): AuthenticationWebFilter {
        val authenticationWebFilter = AuthenticationWebFilter(authenticationManager)
        authenticationWebFilter.setServerAuthenticationConverter(TokenAuthenticationConverter())
        authenticationWebFilter.setRequiresAuthenticationMatcher(FirebaseHeadersExchangeMatcher())
        authenticationWebFilter.setSecurityContextRepository(WebSessionServerSecurityContextRepository())
        return authenticationWebFilter
    }

    @Bean
    fun authenticationManager(auth: FirebaseAuth, saveSessionUserUseCase: SaveSessionUserUseCase): ReactiveAuthenticationManager {
        return FirebaseTokenAuthenticationManager(auth, saveSessionUserUseCase)
    }
}
