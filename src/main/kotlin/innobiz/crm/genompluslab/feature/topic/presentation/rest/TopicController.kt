package innobiz.crm.genompluslab.feature.topic.presentation.rest

import innobiz.crm.genompluslab.core.config.api.Controller
import innobiz.crm.genompluslab.core.config.api.CreateApiResponses
import innobiz.crm.genompluslab.core.config.api.CreateResponseDto
import innobiz.crm.genompluslab.core.config.api.OkApiResponses
import innobiz.crm.genompluslab.feature.analytics.domain.models.TopicWithScore
import innobiz.crm.genompluslab.feature.topic.domain.models.Topic
import innobiz.crm.genompluslab.feature.topic.domain.usecases.*
import innobiz.crm.genompluslab.feature.topic.presentation.dto.TopicDto
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
@RequestMapping("/api/topic")
@Tag(name = "topics", description = "The Topics API")
@SecurityRequirement(name = "security_auth")
class TopicController(
        logger: Logger,
        private val addTopicUseCase: AddTopicUseCase,
        private val getTopicUseCase: GetTopicUseCase,
        private val getAllTopicUseCase: GetAllTopicUseCase,
        private val getTopTopicUseCase: GetTopTopicsUseCase,
        private val updateTopicUseCase: UpdateTopicUseCase,
        private val deleteTopicUseCase: DeleteTopicUseCase
): Controller(logger) {
    @CreateApiResponses
    @PostMapping
    suspend fun create(
            @RequestBody addTopicDto: TopicDto,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<CreateResponseDto> {
        try {
            val response = addTopicUseCase(addTopicDto)
            return HttpStatus.CREATED.response(response, "${request.uri}/${response.id}")
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
    ): ResponseEntity<Topic> {
        try {
            return HttpStatus.OK.response(getTopicUseCase(id))
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
            @RequestBody dto: TopicDto
    ): ResponseEntity<Void> {
        try {
            updateTopicUseCase(id, dto)
            return HttpStatus.OK.response()
        } catch (ex: Exception) {
            val (code, message) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @SecurityRequirement(name = "security_auth")
    @OkApiResponses
    @DeleteMapping("/{id}")
    suspend fun delete(@PathVariable id: String): ResponseEntity<Void> {
        try {
            deleteTopicUseCase(id)
            return HttpStatus.OK.response()
        } catch (ex: Exception) {
            val (code, message) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @CreateApiResponses
    @GetMapping("/get/{cityId}")
    suspend fun getAllTopic(
            @PathVariable cityId: String,
            @RequestParam(required = false, defaultValue = "0")
            page: Int,
            @RequestParam(required = false, defaultValue = "10")
            size: Int,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<Map<String, Any>> {
        try {
            return HttpStatus.OK.response(getAllTopicUseCase(cityId, page, size))
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @CreateApiResponses
    @GetMapping("/top/{cityId}")
    suspend fun getTopTopic(
            @PathVariable cityId: String,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<Collection<TopicWithScore>> {
        try {
            return HttpStatus.OK.response(getTopTopicUseCase(cityId))
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

}