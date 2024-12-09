package innobiz.crm.genompluslab.feature.topic.presentation.rest

import com.dev.course.core.config.api.Controller
import com.dev.course.core.config.api.CreateApiResponses
import com.dev.course.core.config.api.CreateResponseDto
import innobiz.crm.genompluslab.feature.topic.domain.usecases.AddTopicUseCase
import innobiz.crm.genompluslab.feature.topic.presentation.dto.AddTopicDto
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
@RequestMapping("/api/topic")
@Tag(name = "topics", description = "The Topics API")
@SecurityRequirement(name = "security_auth")
class TopicController(
        logger: Logger,
        private val addTopicUseCase: AddTopicUseCase
): Controller(logger) {
    @CreateApiResponses
    @PostMapping
    suspend fun create(
            @RequestBody addTopicDto: AddTopicDto,
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
}