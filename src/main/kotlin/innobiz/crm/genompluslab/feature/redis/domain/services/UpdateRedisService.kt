//package innobiz.crm.genompluslab.feature.redis.domain.services
//
//import innobiz.crm.genompluslab.feature.repositories.AnalyticsAnalysisRepository
//import innobiz.crm.genompluslab.feature.repositories.TopicRepository
//import org.springframework.scheduling.annotation.Scheduled
//import org.springframework.stereotype.Service
//
//@Service
//class UpdateRedisService(
//        private val redisTemplate: RedisTemplate<String, Any>,
//        private val analyticsAnalysisRepository: AnalyticsAnalysisRepository,
//        private val topicRepository: TopicRepository
//) {
//
//    @Scheduled(cron = "0 0 */6 * * *") // Обновление каждые 6 часов
//    fun updateRedisData() {
//    }
//}