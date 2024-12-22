//package innobiz.crm.genompluslab.feature.redis.listner
//
//import innobiz.crm.genompluslab.feature.redis.domain.services.UpdateRedisService
//import org.springframework.stereotype.Component
//
//class PopularityUpdateListener {
//    @Component
//    class PopularityUpdateListener(
//            private val updateRedisService: UpdateRedisService
//    ) {
//
//        @RabbitListener(queues = ["popularity.update"])
//        fun handlePopularityUpdate(analysisId: String) {
//            updateRedisService.updateRedisData() // Обновляем Redis после события
//        }
//    }