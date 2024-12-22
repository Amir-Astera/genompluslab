//package innobiz.crm.genompluslab.feature.redis.domain.usecases
//
//import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
//import innobiz.crm.genompluslab.feature.redis.domain.services.RedisService
//import org.springframework.stereotype.Service
//
//interface RedisSearchUseCase {
//    suspend operator fun invoke(text: String, limit: Int, offset: Int): Collection<Analysis>
//}
//
//@Service
//internal class RedisSearchUseCaseImpl(
//    private val redisService: RedisService,
//): RedisSearchUseCase {
//    override suspend fun invoke(text: String, limit: Int, offset: Int): Collection<Analysis> {
//        return if (redisService.isTopic(text)) {
//            redisService.searchByTopic(text, limit, offset)
//        } else {
//            redisService.searchByAnalyses(text, limit, offset)
//        }
//    }
//}
//
