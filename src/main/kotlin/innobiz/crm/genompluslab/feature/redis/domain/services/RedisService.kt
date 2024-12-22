//package innobiz.crm.genompluslab.feature.redis.domain.services
//
//import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
//import org.springframework.stereotype.Service
//
//interface RedisService {
//    suspend fun getPopularTopics()
//    suspend fun getPopularAnalysis()
////    suspend fun search(query: String, limit: Int, offset: Int): Collection<Analysis>
//    suspend fun searchByTopic(topicId: String, limit: Int, offset: Int): List<Analysis>
//    suspend fun searchByAnalyses(text: String, limit: Int, offset: Int): List<Analysis>
//    suspend fun isTopic(text: String): Boolean
//}
//
//@Service
//internal class RedisServiceImpl(
////        private val redisTemplate: RedisTemplate<String, Any>
//): RedisService {
//    override suspend fun getPopularTopics() {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun getPopularAnalysis() {//тут возвращаем максимум 10 популярных
//        TODO("Not yet implemented")
//    }
//
//}