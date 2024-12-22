//package innobiz.crm.genompluslab.feature.redis.presentation.rest
//
//import innobiz.crm.genompluslab.core.config.api.Controller
//import innobiz.crm.genompluslab.feature.users.domain.models.UserAggregate
//import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
//import innobiz.crm.genompluslab.feature.redis.domain.usecases.RedisSearchUseCase
//import io.swagger.v3.oas.annotations.media.Content
//import io.swagger.v3.oas.annotations.media.Schema
//import io.swagger.v3.oas.annotations.responses.ApiResponse
//import io.swagger.v3.oas.annotations.responses.ApiResponses
//import io.swagger.v3.oas.annotations.tags.Tag
//import org.slf4j.Logger
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.*
//import org.springframework.web.server.ResponseStatusException
//
//@RestController
//@RequestMapping("/api/search")
//@Tag(name = "search", description = "The Search API")
//class UserController(
//        logger: Logger,
//        private val searchUseCase: RedisSearchUseCase
//): Controller(logger) {
////    @ApiResponses(
////            ApiResponse(responseCode = "200", description = "ok",
////                    content = [Content(schema = Schema(implementation = UserAggregate::class))])
////    )
//    @GetMapping
//    suspend fun search(
//        @RequestParam text: String,
//        @RequestParam(defaultValue = "10") limit: Int,
//        @RequestParam(defaultValue = "0") offset: Int
//    ): ResponseEntity<Collection<Analysis>> {
//        try {
//            return HttpStatus.OK.response(searchUseCase(text, limit, offset))
//        } catch (ex: Exception) {
//            val (code, message) = getError(ex)
//            throw ResponseStatusException(code, message)
//        }
//    }
//}