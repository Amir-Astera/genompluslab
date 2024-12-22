package innobiz.crm.genompluslab.feature.city.domain.usecases

import innobiz.crm.genompluslab.feature.city.domain.models.City
import innobiz.crm.genompluslab.feature.city.domain.services.CityService
import org.springframework.stereotype.Service

interface GetAllCityUseCase {
    suspend operator fun invoke(): Collection<City>
}

@Service
internal class GetAllCityUseCaseImpl(
    private val cityService: CityService
): GetAllCityUseCase {
    override suspend fun invoke(): Collection<City> {
        return cityService.getAll()
    }

}