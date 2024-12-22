package innobiz.crm.genompluslab.feature.city.domain.services

import innobiz.crm.genompluslab.core.extension.toEntity
import innobiz.crm.genompluslab.core.extension.toModel
import innobiz.crm.genompluslab.feature.city.domain.errors.CityNotFoundException
import innobiz.crm.genompluslab.feature.city.domain.models.City
import innobiz.crm.genompluslab.feature.repositories.CityRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

interface CityService {
    suspend fun save(city: City)
    suspend fun getAll(): Collection<City>
    suspend fun get(cityId: String): City
}

@Service
internal class CityServiceImpl(
        private val cityRepository: CityRepository
): CityService {
    override suspend fun save(city: City) {
        cityRepository.save(city.toEntity())
    }

    override suspend fun getAll(): Collection<City> {
        //TODO реализовать пагинацию, не сделано из-за малого количество
        return cityRepository.findAll().map { it.toModel() }.toList()
    }

    override suspend fun get(cityId: String): City {
        return cityRepository.findById(cityId)?.toModel() ?: throw CityNotFoundException(cityId)
    }

}