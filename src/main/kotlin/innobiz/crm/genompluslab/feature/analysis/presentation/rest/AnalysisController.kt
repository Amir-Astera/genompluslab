package innobiz.crm.genompluslab.feature.analysis.presentation.rest

import innobiz.crm.genompluslab.core.config.api.Controller
import innobiz.crm.genompluslab.core.config.api.CreateApiResponses
import innobiz.crm.genompluslab.core.config.api.CreateResponseDto
import innobiz.crm.genompluslab.core.config.api.OkApiResponses
import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
import innobiz.crm.genompluslab.feature.analysis.domain.usecases.*
import innobiz.crm.genompluslab.feature.analysis.presentation.dto.AddAnalysisDto
import innobiz.crm.genompluslab.feature.analysis.presentation.dto.UpdateAnalysisDto
import innobiz.crm.genompluslab.feature.analysis.presentation.dto.UpdatePriceByPercentageDto
import innobiz.crm.genompluslab.feature.analytics.domain.models.AnalysisWithPopularity
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/analysis")
@Tag(name = "analysis", description = "The Analysis API")
@SecurityRequirement(name = "security_auth")
class AnalysisController(
        logger: Logger,
        private val addAnalysisUseCase: AddAnalysisUseCase,
        private val getAnalysisUseCase: GetAnalysisUseCase,
        private val getAllAnalysisByTopicUseCase: GetAllAnalysisByTopicUseCase,
        private val getTopAnalysesUseCase: GetTopAnalysesUseCase,
        private val deleteAnalysisUseCase: DeleteAnalysisUseCase,
        private val updateAllPricesByPercentage: UpdateAllPricesByPercentage,
        private val updateAnalysisUseCase: UpdateAnalysisUseCase,
        private val searchAnalysisUseCase: SearchAnalysisUseCase
): Controller(logger) {
    @CreateApiResponses
    @PostMapping
    suspend fun create(
            @RequestBody dto: AddAnalysisDto,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<CreateResponseDto> {
        try {
            val response = addAnalysisUseCase(dto)
            return HttpStatus.OK.response()
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @CreateApiResponses
    @GetMapping("/{id}")
    suspend fun get(
            @PathVariable id: String,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<Analysis> {
        try {
            return HttpStatus.OK.response(getAnalysisUseCase(id))
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @CreateApiResponses
    @GetMapping("/byTopic/{cityId}/{topicId}")
    suspend fun getAllByTopic(
            @PathVariable cityId: String,
            @PathVariable topicId: String,
            @RequestParam(required = false, defaultValue = "0")
            page: Int,
            @RequestParam(required = false, defaultValue = "10")
            size: Int,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<Map<String, Any>> {
        try {
            return HttpStatus.OK.response(getAllAnalysisByTopicUseCase(cityId, topicId, page, size))
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @CreateApiResponses
    @GetMapping("/popular/{cityId}")
    suspend fun getPopular(
            @PathVariable cityId: String,
    ): ResponseEntity<Collection<AnalysisWithPopularity>> {
        try {
            return HttpStatus.OK.response(getTopAnalysesUseCase(cityId))
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @SecurityRequirement(name = "security_auth")
    @OkApiResponses
    @PutMapping("/{id}")
    suspend fun update(
            @PathVariable id: String,
            @RequestBody dto: UpdateAnalysisDto
    ): ResponseEntity<Void> {
        try {
            updateAnalysisUseCase(id, dto)
            return HttpStatus.OK.response()
        } catch (ex: Exception) {
            val (code, message) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @OkApiResponses
    @PutMapping("/byPercentage")
    suspend fun updateByPercentage(
            @RequestBody dto: UpdatePriceByPercentageDto
    ): ResponseEntity<Void> {
        try {
            updateAllPricesByPercentage(dto)
            return HttpStatus.OK.response()
        } catch (ex: Exception) {
            val (code, message) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @SecurityRequirement(name = "security_auth")
    @OkApiResponses
    @DeleteMapping("/{id}")
    suspend fun delete(
            @PathVariable id: String
    ): ResponseEntity<Void> {
        try {
            deleteAnalysisUseCase(id)
            return HttpStatus.OK.response()
        } catch (ex: Exception) {
            val (code, message) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @CreateApiResponses
    @GetMapping("/search/{cityId}")
    suspend fun searchAnalysis(
            @PathVariable cityId: String,
            @RequestParam name: String,
            @RequestParam(required = false, defaultValue = "0")
            page: Int,
            @RequestParam(required = false, defaultValue = "10")
            size: Int,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<Map<String, Any>> {
        try {
            return HttpStatus.OK.response(searchAnalysisUseCase(cityId, name, page, size))
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

}