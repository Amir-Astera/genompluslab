package innobiz.crm.genompluslab.feature.admin.presentation.rest

import innobiz.crm.genompluslab.core.config.api.Controller
import innobiz.crm.genompluslab.core.config.api.CreateApiResponses
import innobiz.crm.genompluslab.core.config.api.CreateResponseDto
import innobiz.crm.genompluslab.feature.admin.domain.usecases.ContinueCreateOrderToLisUseCase
import innobiz.crm.genompluslab.feature.admin.presentation.dto.OrderWithIdsDto
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/registry")
@Tag(name = "registry", description = "The Registry API")
@SecurityRequirement(name = "security_auth")
class RegistryController(
        logger: Logger,
        private val continueCreateOrderToLisUseCase: ContinueCreateOrderToLisUseCase
): Controller(logger) {
    @CreateApiResponses
    @PostMapping
    suspend fun create(
            @RequestBody dto: OrderWithIdsDto,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<CreateResponseDto> {
        try {
            val response = continueCreateOrderToLisUseCase(dto)
            return HttpStatus.OK.response()
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }
}