package innobiz.crm.genompluslab.feature.city.presentation.rest

import innobiz.crm.genompluslab.core.config.api.Controller
import innobiz.crm.genompluslab.core.config.api.CreateApiResponses
import innobiz.crm.genompluslab.core.config.api.CreateResponseDto
import innobiz.crm.genompluslab.feature.city.domain.models.City
import innobiz.crm.genompluslab.feature.city.domain.usecases.AddCityUseCase
import innobiz.crm.genompluslab.feature.city.domain.usecases.GetAllCityUseCase
import innobiz.crm.genompluslab.feature.city.presentation.dto.CityDto
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/city")
@Tag(name = "city", description = "The City API")
@SecurityRequirement(name = "security_auth")
class CityController(
        logger: Logger,
        private val addCityUseCase: AddCityUseCase,
        private val getAllCityUseCase: GetAllCityUseCase
): Controller(logger) {
    @CreateApiResponses
    @PostMapping
    suspend fun create(
            @RequestBody dto: CityDto,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<CreateResponseDto> {
        try {
            addCityUseCase(dto)
            return HttpStatus.OK.response()
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @CreateApiResponses
    @GetMapping("/all")
    suspend fun getAll(
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<Collection<City>> {
        try {
            return HttpStatus.OK.response(getAllCityUseCase())
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }
}