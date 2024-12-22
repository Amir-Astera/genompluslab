package innobiz.crm.genompluslab.feature.city.domain.errors

class CityNotFoundException(val cityId: String): RuntimeException("City with id: $cityId not found!")