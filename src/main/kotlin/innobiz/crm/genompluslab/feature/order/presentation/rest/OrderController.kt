package innobiz.crm.genompluslab.feature.order.presentation.rest

import innobiz.crm.genompluslab.core.config.api.Controller
import innobiz.crm.genompluslab.core.config.api.CreateResponseDto
import innobiz.crm.genompluslab.feature.admin.domain.usecases.ContinueCreateOrderToLisUseCase
import innobiz.crm.genompluslab.feature.order.domain.usecases.CreateOrderUseCase
import innobiz.crm.genompluslab.feature.order.presentation.dto.CreateOrderDto
import innobiz.crm.genompluslab.feature.patient.presentation.dto.UserPatientDto
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
@RequestMapping("/api/orders")
@Tag(name = "orders", description = "The Orders API")
class OrderController(
        logger: Logger,
        private val createOrderUseCase: CreateOrderUseCase
): Controller(logger) {
    @SecurityRequirement(name = "security_auth")
    @PostMapping("/save")
    suspend fun create(
            @RequestBody dto: CreateOrderDto,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<Any> {
        try {
            val response = createOrderUseCase(dto)
            return HttpStatus.CREATED.response(response)
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }
}