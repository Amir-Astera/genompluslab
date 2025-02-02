package innobiz.crm.genompluslab.feature.admin.domain.usecases

import innobiz.crm.genompluslab.core.config.enums.OrderAnalysisStatus
import innobiz.crm.genompluslab.core.config.enums.OrderStatus
import innobiz.crm.genompluslab.core.extension.toModel
import innobiz.crm.genompluslab.feature.admin.domain.errors.ContinueCreateOrderException
import innobiz.crm.genompluslab.feature.admin.domain.errors.MaterialIncorrectException
import innobiz.crm.genompluslab.feature.admin.domain.services.RegistryService
import innobiz.crm.genompluslab.feature.admin.presentation.dto.OrderWithIdsDto
import innobiz.crm.genompluslab.feature.analysis.domain.services.AnalysisService
import innobiz.crm.genompluslab.feature.order.domain.services.OrderService
import innobiz.crm.genompluslab.feature.repositories.OrderAnalysisRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

interface ContinueCreateOrderToLisUseCase {
    suspend operator fun invoke(dto: OrderWithIdsDto)
}

@Service
internal class ContinueCreateOrderToLisUseCaseImpl(
        private val registryService: RegistryService,
        private val orderService: OrderService,
        private val orderAnalysisRepository: OrderAnalysisRepository,
        private val analysisService: AnalysisService
): ContinueCreateOrderToLisUseCase {
    override suspend fun invoke(dto: OrderWithIdsDto) {
        //TODO /api/weblab/IS_IDS_VALID
        //TODO сделать проверку на правильность материала и айдс
        val order = orderService.get(dto.orderId)
        if (order.status != OrderStatus.ORDERED) throw ContinueCreateOrderException()

        val orderAnalysisEntities  = orderAnalysisRepository.findAllByOrderId(order.id).toList()
        val analysisIds = orderAnalysisEntities.map { it.analysisId }.toSet()
        val analyses = analysisService.getAllByIds(analysisIds)
        val analysisMap = analyses.associateBy { it.id }
        val orderAnalyses = orderAnalysisEntities.map { entity ->
            val analysis = analysisMap[entity.analysisId]!!
            entity.toModel(order, analysis)
        }

//        val analyses = analysisService.getAllByIds(orderAnalyses.map { it.analysisId }.toList())

        analyses.forEach { analysis ->
            if (dto.materials.none { it.materialId == analysis.materialId || it.keyId == analysis.materialKeyId }) throw MaterialIncorrectException()
        }

        val orderAnalysesWithIds = dto.materials
                .groupBy { it.keyId to it.ids }  // Группируем по уникальной паре (keyId, ids)
                .map { (keyIdAndIds, materialsGroup) ->
                    val (keyId, ids) = keyIdAndIds
                    // Создаем researchId один раз для данной комбинации keyId и ids
                    val newResearchId = registryService.addMaterialWithIds(order.internalId, keyId, ids)

                    // Для каждого материала из группы добавляем сервис и обрабатываем заказ
                    materialsGroup.forEach { material ->
                        registryService.addServiceToMaterial(newResearchId, material.materialId)
                    }
                    registryService.processOrder(newResearchId, ids)
                    orderAnalyses
                            .filter { orderAnalysis ->
                                orderAnalysis.analysis.materialKeyId == keyId &&
                                        materialsGroup.any { it.materialId == orderAnalysis.analysis.materialId }
                            }
                            .map { it.copy(status = OrderAnalysisStatus.PENDING, ids = ids) }
                            .toList()
                }.flatten()
        registryService.createRecordForOrder(order.internalId)
        registryService.postProcess(order.internalId)

        val newOrder = order.copy(status = OrderStatus.PROCESSING)
        orderService.save(newOrder, null, orderAnalysesWithIds)
    }
}