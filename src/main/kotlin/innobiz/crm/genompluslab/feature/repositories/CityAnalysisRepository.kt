package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.feature.city.data.CityAnalysisEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CityAnalysisRepository: CoroutineCrudRepository<CityAnalysisEntity, String> {
}