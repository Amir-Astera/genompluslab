package innobiz.crm.genompluslab.feature.analytics.presentation.rest

import innobiz.crm.genompluslab.core.config.api.Controller
import innobiz.crm.genompluslab.core.config.api.CreateApiResponses
import innobiz.crm.genompluslab.core.config.api.CreateResponseDto
import innobiz.crm.genompluslab.feature.analysis.presentation.dto.AddAnalysisDto
import innobiz.crm.genompluslab.feature.analytics.domain.usecases.IncrementViewUseCase
import innobiz.crm.genompluslab.feature.analytics.presentation.dto.IncrementViewDto
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
@RequestMapping("/api/analytics")
@Tag(name = "analytics", description = "The Analytics API")
@SecurityRequirement(name = "security_auth")
class AnalyticsController(
        logger: Logger,
        private val incrementViewUseCase: IncrementViewUseCase
): Controller(logger) {
    @CreateApiResponses
    @PostMapping("/view")
    suspend fun incrementView(
            @RequestBody dto: IncrementViewDto,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<CreateResponseDto> {
        try {
            incrementViewUseCase(dto)
            return HttpStatus.OK.response()
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }
}