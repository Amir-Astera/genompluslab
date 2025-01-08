package innobiz.crm.genompluslab.feature.cart.presentation.rest

import innobiz.crm.genompluslab.core.config.api.Controller
import innobiz.crm.genompluslab.core.config.api.CreateApiResponses
import innobiz.crm.genompluslab.core.config.api.CreateResponseDto
import innobiz.crm.genompluslab.core.config.api.OkApiResponses
import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
import innobiz.crm.genompluslab.feature.cart.domain.usecases.*
import innobiz.crm.genompluslab.feature.cart.presentation.dto.AddAnalysisToCartDto
import innobiz.crm.genompluslab.feature.cart.presentation.dto.ChangeStatusCartDto
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/cart")
@Tag(name = "cart", description = "The Cart API")
@SecurityRequirement(name = "security_auth")
class CartController(
        logger: Logger,
        private val addAnalysisToCartUseCase: AddAnalysisToCartUseCase,
        private val deleteAnalysisInCartUseCase: DeleteAnalysisInCartUseCase,
        private val getCartUseCase: GetCartUseCase,
        //TODO Планируется оплату сделать через каспи и оплату сделать на фронте и использовать статусы на фронте по оплате
        private val changeStatusToWaitingUseCase: ChangeStatusToWaitingUseCase,
        private val changeStatusToPaidUseCase: ChangeStatusToPaidUseCase,
        private val changeStatusToInUseCase: ChangeStatusToInUseCase
): Controller(logger) {
    @SecurityRequirement(name = "security_auth")
    @CreateApiResponses
    @PostMapping
    suspend fun create(
            @RequestBody dto: AddAnalysisToCartDto,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<CreateResponseDto> {
        try {
            addAnalysisToCartUseCase(dto)
            return HttpStatus.OK.response()
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @SecurityRequirement(name = "security_auth")
    @CreateApiResponses
    @GetMapping("/{userId}")
    suspend fun get(
            @PathVariable userId: String,
            @AuthenticationPrincipal userDetails: UserDetails,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<Map<String, Any>> {
        try {
            println(userDetails.username)
            return HttpStatus.OK.response(getCartUseCase(userId))
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @SecurityRequirement(name = "security_auth")
    @OkApiResponses
    @DeleteMapping("/{userId}/{analysisId}")
    suspend fun delete(
            @PathVariable userId: String,
            @PathVariable analysisId: String
    ): ResponseEntity<Void> {
        try {
            deleteAnalysisInCartUseCase(userId, analysisId)
            return HttpStatus.OK.response()
        } catch (ex: Exception) {
            val (code, message) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @SecurityRequirement(name = "security_auth")
    @CreateApiResponses
    @PostMapping("/paid")
    suspend fun changeStatusToPaid(
            @RequestBody dto: ChangeStatusCartDto,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<CreateResponseDto> {
        try {
            changeStatusToPaidUseCase(dto)
            return HttpStatus.OK.response()
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @SecurityRequirement(name = "security_auth")
    @CreateApiResponses
    @PostMapping("/waiting")
    suspend fun changeStatusToWaiting(
            @RequestBody dto: ChangeStatusCartDto,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<CreateResponseDto> {
        try {
            changeStatusToWaitingUseCase(dto)
            return HttpStatus.OK.response()
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @SecurityRequirement(name = "security_auth")
    @CreateApiResponses
    @PostMapping("/in")
    suspend fun changeStatusToIn(
            @RequestBody dto: ChangeStatusCartDto,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<CreateResponseDto> {
        try {
            changeStatusToInUseCase(dto)
            return HttpStatus.OK.response()
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }
}