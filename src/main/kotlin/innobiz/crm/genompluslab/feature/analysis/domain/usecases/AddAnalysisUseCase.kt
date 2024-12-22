package innobiz.crm.genompluslab.feature.analysis.domain.usecases

import innobiz.crm.genompluslab.core.config.api.CreateResponseDto
import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
import innobiz.crm.genompluslab.feature.analysis.domain.services.AnalysisService
import innobiz.crm.genompluslab.feature.analysis.presentation.dto.AddAnalysisDto
import innobiz.crm.genompluslab.feature.city.domain.services.CityService
import innobiz.crm.genompluslab.feature.topic.domain.services.TopicService
import org.springframework.stereotype.Service


interface AddAnalysisUseCase {
    suspend operator fun invoke(dto: AddAnalysisDto)
}
@Service
class AddAnalysisUseCaseImpl(
        private val analysisService: AnalysisService,
        private val cityService: CityService,
        private val topicService: TopicService
): AddAnalysisUseCase {
    override suspend fun invoke(dto: AddAnalysisDto) {
        val city = cityService.get(dto.cityId)
        val topic = topicService.get(dto.topicId)
        val analysis = Analysis(
                code = dto.code,
                name = dto.name,
                material = dto.material,
                deadline = dto.deadline,
                price = dto.price,
                description = dto.description
        )
        analysisService.save(analysis, topic, city)
    }
}