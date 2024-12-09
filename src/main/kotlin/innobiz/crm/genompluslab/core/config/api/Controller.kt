package com.dev.course.core.config.api

import com.dev.course.feature.authority.domain.errors.AuthorityDuplicateNameException
import com.dev.course.feature.authority.domain.errors.AuthorityNotFoundException
import com.dev.course.feature.authorization.domain.errors.FirebaseAuthException
import com.dev.course.feature.lesson.domain.errors.LessonNotFoundException
import com.dev.course.feature.modules.domain.errors.ModuleNotFoundException
import com.dev.course.feature.users.domain.errors.AdminAuthorityNotFoundException
import com.dev.course.feature.users.domain.errors.UserDuplicateLoginException
import com.dev.course.feature.users.domain.errors.UserNotFoundException
import com.dev.course.feature.users.domain.errors.UserPartnersNotFoundException
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI

abstract class Controller(val logger: Logger) {

        protected fun <T> HttpStatus.response(body: T, location: String): ResponseEntity<T> =
                ResponseEntity.status(this).location(URI.create(location)).body(body)

        protected fun <T> HttpStatus.response(): ResponseEntity<T> =
                ResponseEntity.status(this).build()

        protected fun <T> HttpStatus.response(body: T): ResponseEntity<T> =
                ResponseEntity.status(this).body(body)

        protected fun getError(ex: Exception) =
                when (ex) {
                    is FirebaseAuthException -> Pair(HttpStatus.UNAUTHORIZED, ex.message)
                    is UserNotFoundException,
                    is UserPartnersNotFoundException,
                    is AuthorityNotFoundException,
                    is ModuleNotFoundException,
                    is LessonNotFoundException,
                    is AdminAuthorityNotFoundException,
                    is AuthorityDuplicateNameException,
                    is UserDuplicateLoginException -> Pair(HttpStatus.CONFLICT, ex.message)
                        else -> {
                                logger.error("Unhandled error", ex)
                                Pair(HttpStatus.INTERNAL_SERVER_ERROR, ex.message)
                        }
                }
}

@ApiResponses(
        ApiResponse(responseCode = "404", description = "not found", content = [Content(schema = Schema(implementation = Void::class))]),
        ApiResponse(responseCode = "500", description = "internal server error", content = [Content(schema = Schema(implementation = Void::class))])
)
annotation class GetApiResponses

@ApiResponses(
        ApiResponse(responseCode = "201", description = "created", content = [Content(schema = Schema(implementation = CreateResponseDto::class))]),
        ApiResponse(responseCode = "400", description = "bad request", content = [Content(schema = Schema(implementation = Void::class))]),
        ApiResponse(responseCode = "500", description = "internal server error", content = [Content(schema = Schema(implementation = Void::class))])
)
annotation class CreateApiResponses

@ApiResponses(
        ApiResponse(responseCode = "200", description = "ok", content = [Content(schema = Schema(implementation = Void::class))]),
        ApiResponse(responseCode = "404", description = "not found", content = [Content(schema = Schema(implementation = Void::class))]),
        ApiResponse(responseCode = "500", description = "internal server error", content = [Content(schema = Schema(implementation = Void::class))])
)
annotation class OkApiResponses

@ApiResponses(
        ApiResponse(responseCode = "400", description = "bad request", content = [Content(schema = Schema(implementation = Void::class))]),
        ApiResponse(responseCode = "404", description = "not found", content = [Content(schema = Schema(implementation = Void::class))]),
        ApiResponse(responseCode = "504", description = "gateway error", content = [Content(schema = Schema(implementation = Void::class))]),
        ApiResponse(responseCode = "500", description = "internal server error", content = [Content(schema = Schema(implementation = Void::class))])
)
annotation class ExtendedOkApiResponses
