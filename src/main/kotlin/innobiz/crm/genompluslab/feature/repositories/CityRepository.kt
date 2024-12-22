package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.feature.city.data.CityEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface CityRepository: CoroutineCrudRepository<CityEntity, String> {
}