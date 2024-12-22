package innobiz.crm.genompluslab.feature.analysis.domain.usecases

import innobiz.crm.genompluslab.feature.analysis.domain.services.AnalysisService
import innobiz.crm.genompluslab.feature.analysis.presentation.dto.UpdateAnalysisDto
import innobiz.crm.genompluslab.feature.city.domain.services.CityService
import innobiz.crm.genompluslab.feature.topic.domain.services.TopicService
import org.springframework.stereotype.Service

interface UpdateAnalysisUseCase {
    suspend operator fun invoke(id: String, dto: UpdateAnalysisDto)
}
@Service
class UpdateAnalysisUseCaseImpl(
        private val analysisService: AnalysisService,
        private val cityService: CityService,
        private val topicService: TopicService
): UpdateAnalysisUseCase {
    override suspend fun invoke(id: String, dto: UpdateAnalysisDto) {
        val city = cityService.get(dto.cityId)
        val topic = topicService.get(dto.topicId)
        val oldEntity = analysisService.get(id)
        val newEntity = oldEntity.copy(
                code = dto.code ?: oldEntity.code,
                name = dto.name ?: oldEntity.name,
                material = dto.material ?: oldEntity.material,
                deadline = dto.deadline ?: oldEntity.deadline,
                price = dto.price ?: oldEntity.price
        )
        analysisService.save(newEntity, topic, city)
    }

}