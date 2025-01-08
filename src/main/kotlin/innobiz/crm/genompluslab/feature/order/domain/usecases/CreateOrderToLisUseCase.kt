package innobiz.crm.genompluslab.feature.order.domain.usecases

import innobiz.crm.genompluslab.core.extension.toModel
import innobiz.crm.genompluslab.feature.analysis.domain.errors.AnalysisNotFoundException
import innobiz.crm.genompluslab.feature.order.domain.services.OrderService
import innobiz.crm.genompluslab.feature.order.presentation.dto.CreatePatientOrderDto
import innobiz.crm.genompluslab.feature.patient.domain.errors.PatientNotFoundException
import innobiz.crm.genompluslab.feature.repositories.AnalysisRepository
import innobiz.crm.genompluslab.feature.repositories.OrderAnalysisRepository
import innobiz.crm.genompluslab.feature.repositories.PatientRepository
import org.springframework.stereotype.Service

interface CreateOrderToLisUseCase {
    suspend operator fun invoke(dto: CreatePatientOrderDto)
}

@Service
internal class CreateOrderToLisUseCaseImpl(
        private val orderService: OrderService,
        private val patientRepository: PatientRepository,
        private val analysisRepository: AnalysisRepository
): CreateOrderToLisUseCase {
    override suspend fun invoke(dto: CreatePatientOrderDto) {
        val patient = patientRepository.findById(dto.patientId)?.toModel() ?: throw PatientNotFoundException(dto.patientId)
        val order = orderService.get(dto.orderId)
        val analyses = analysisRepository.findById(dto.analysisId)?.toModel() ?: throw AnalysisNotFoundException(dto.analysisId)
        val a = orderService.addMaterialWithIds(order.internalId, patient, analyses, dto.ids)
    }

}