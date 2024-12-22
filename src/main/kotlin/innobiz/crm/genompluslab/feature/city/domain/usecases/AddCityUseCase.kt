package innobiz.crm.genompluslab.feature.city.domain.usecases

import innobiz.crm.genompluslab.feature.city.domain.models.City
import innobiz.crm.genompluslab.feature.city.domain.services.CityService
import innobiz.crm.genompluslab.feature.city.presentation.dto.CityDto
import org.springframework.stereotype.Service

interface AddCityUseCase {
    suspend operator fun invoke(dto: CityDto)
}

@Service
internal class AddCityUseCaseImpl(
        private val cityService: CityService
): AddCityUseCase {
    override suspend fun invoke(dto: CityDto) {
        cityService.save(City(
                name = dto.name
        ))
    }

}