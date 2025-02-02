package innobiz.crm.genompluslab.feature.patient.presentation.rest

import innobiz.crm.genompluslab.core.config.api.Controller
import innobiz.crm.genompluslab.core.config.api.CreateApiResponses
import innobiz.crm.genompluslab.core.config.api.CreateResponseDto
import innobiz.crm.genompluslab.feature.patient.domain.models.Patient
import innobiz.crm.genompluslab.feature.patient.domain.usecases.AddPatientUseCase
import innobiz.crm.genompluslab.feature.patient.domain.usecases.GetByIINUseCase
import innobiz.crm.genompluslab.feature.patient.domain.usecases.GetPatientUseCase
import innobiz.crm.genompluslab.feature.patient.domain.usecases.GetPhoneByIinUseCase
import innobiz.crm.genompluslab.feature.patient.presentation.dto.GetPatientDto
import innobiz.crm.genompluslab.feature.patient.presentation.dto.GetUserPatientDto
import innobiz.crm.genompluslab.feature.patient.presentation.dto.PatientPhoneDto
import innobiz.crm.genompluslab.feature.patient.presentation.dto.UserPatientDto
import innobiz.crm.genompluslab.feature.users.domain.usecases.*
import innobiz.crm.genompluslab.feature.users.presentation.dto.CreateUserDto
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/api/patient")
@Tag(name = "patients", description = "The Patients API")
class PatientController(
        logger: Logger,
        private val addPatientUseCase: AddPatientUseCase,
        private val getPatientByIINUseCase: GetByIINUseCase,
        private val getPhoneByIinUseCase: GetPhoneByIinUseCase,
        private val getPatientUseCase: GetPatientUseCase
): Controller(logger) {

//    @SecurityRequirement(name = "security_auth")
//    @CreateApiResponses
    @PostMapping("/save")
    suspend fun create(
            @RequestBody createUser: UserPatientDto,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<CreateResponseDto> {
        try {
            val response = addPatientUseCase(createUser)
            return HttpStatus.CREATED.response(response, "${request.uri}/${response.id}")
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @SecurityRequirement(name = "security_auth")
    @CreateApiResponses
    @PostMapping("/check/iin")
    suspend fun checkByIin(
            @RequestBody(required = true) dto: GetPatientDto,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<Boolean> {
        try {
            val response = getPatientByIINUseCase(dto.iin)
            return HttpStatus.CREATED.response(response)
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @SecurityRequirement(name = "security_auth")
    @CreateApiResponses
    @PostMapping("/iin")
    suspend fun getByIin(
            @Parameter(hidden = true) request: ServerHttpRequest,
            @Parameter(hidden = true) exchange: ServerWebExchange
    ): ResponseEntity<GetUserPatientDto> {
        try {
            val response = getPatientUseCase(exchange = exchange)
            return HttpStatus.CREATED.response(response)
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

    @SecurityRequirement(name = "security_auth")
    @CreateApiResponses
    @PostMapping("/phone")
    suspend fun getByPhone(
            @RequestBody(required = true) dto: GetPatientDto,
            @Parameter(hidden = true) request: ServerHttpRequest
    ): ResponseEntity<PatientPhoneDto> {
        try {
            val response = getPhoneByIinUseCase(dto.iin)
            return HttpStatus.CREATED.response(response)
        } catch (ex: Exception) {
            val (code: HttpStatus, message: String?) = getError(ex)
            throw ResponseStatusException(code, message)
        }
    }

}