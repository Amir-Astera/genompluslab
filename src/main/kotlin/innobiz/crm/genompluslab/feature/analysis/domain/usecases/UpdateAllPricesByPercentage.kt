package innobiz.crm.genompluslab.feature.analysis.domain.usecases

import innobiz.crm.genompluslab.feature.analysis.domain.errors.GetAllAnalysisNotFoundException
import innobiz.crm.genompluslab.feature.analysis.domain.services.AnalysisService
import innobiz.crm.genompluslab.feature.analysis.presentation.dto.UpdatePriceByPercentageDto
import org.springframework.stereotype.Service

interface UpdateAllPricesByPercentage {
    suspend operator fun invoke(dto: UpdatePriceByPercentageDto)
}

@Service
class UpdateAllPricesByPercentageImpl(
    private val analysisService: AnalysisService
): UpdateAllPricesByPercentage {
    override suspend fun invoke(dto: UpdatePriceByPercentageDto) {
        //TODO у каждой клиники свои усулги и их цены
        //TODO здесь цены за сайт указывается, а для клиник основываемся от цен основых цены
        //TODO есть основная цены - это цены лаборатории, есть цена сайта - чуть дороже, есть цена за клиники - это со скидками
        val analysis = analysisService.getAllByIds(dto.analysisIds)
        if (analysis.isEmpty()) throw GetAllAnalysisNotFoundException()
        val newEntities = analysis.map {
            it.copy(
                    price = it.price * (1 + dto.price / 100)
            )
        }
        analysisService.saveAll(newEntities)
    }

}