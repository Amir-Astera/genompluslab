package innobiz.crm.genompluslab.core.extension

import innobiz.crm.genompluslab.feature.authority.data.entity.AuthorityEntity
import innobiz.crm.genompluslab.feature.users.data.entity.UserEntity
import innobiz.crm.genompluslab.feature.users.domain.models.UserAggregate
import innobiz.crm.genompluslab.feature.analysis.data.AnalysisEntity
import innobiz.crm.genompluslab.feature.analysis.domain.models.Analysis
import innobiz.crm.genompluslab.feature.analytics.data.AnalyticsAnalysisEntity
import innobiz.crm.genompluslab.feature.analytics.domain.models.AnalyticsAnalysis
import innobiz.crm.genompluslab.feature.authority.domain.models.Authority
import innobiz.crm.genompluslab.feature.cart.data.CartEntity
import innobiz.crm.genompluslab.feature.cart.domain.models.Cart
import innobiz.crm.genompluslab.feature.city.data.CityEntity
import innobiz.crm.genompluslab.feature.city.domain.models.City
import innobiz.crm.genompluslab.feature.files.data.FileEntity
import innobiz.crm.genompluslab.feature.files.domain.models.File
import innobiz.crm.genompluslab.feature.topic.data.TopicAnalysisEntity
import innobiz.crm.genompluslab.feature.topic.data.TopicEntity
import innobiz.crm.genompluslab.feature.topic.domain.models.Topic
import java.time.LocalDateTime

fun UserEntity.toModel(
        authorities: Collection<Authority>,

): UserAggregate {
    return UserAggregate(
            id = id,
            name = name,
            surname = surname,
            email = email,
            phone = phone,
            login = login,
            authorities = authorities,
            logoUrl = logo,
            version = version,
            createdAt = createdAt,
            updatedAt = updatedAt
    )
}
//
fun AuthorityEntity.toModel(
        version: Long? = null,
        createdAt: LocalDateTime? = null
): Authority {
    return Authority(
            id = id,
            name = name,
            description = description,
            version = version ?: this.version,
            createdAt = createdAt ?: this.createdAt,
            updatedAt = updatedAt
    )
}

fun Authority.toEntity(): AuthorityEntity {
    return AuthorityEntity(
            id = id,
            name = name,
            description = description,
            version = version,
            createdAt = createdAt,
            updatedAt = updatedAt
    )
}

fun UserAggregate.toEntity(): UserEntity {
    return UserEntity(
            id = id,
            name = name,
            surname = surname,
            email = email,
            phone = phone,
            login = login,
            logo = logoUrl,
            version = version,
            createdAt = createdAt,
            updatedAt = updatedAt
    )
}

fun FileEntity.toModel(): File {
    return File(
            id = id,
            directory = directory,
            format = format,
            url = url
    )
}

fun File.toEntity(): FileEntity {
    return FileEntity(
            id = id,
            directory = directory,
            format = format,
            url = url,
            version = version,
            createdAt = LocalDateTime.now()
    )
}

fun TopicEntity.toModel(): Topic {
    return Topic(
            id = id,
            name = name
    )
}

fun Topic.toEntity(): TopicEntity {
    return TopicEntity(
            id = id,
            name = name,
            version = version,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
    )
}

fun AnalysisEntity.toModel(): Analysis {
    return Analysis(
            id = id,
            code = code,
            name = name,
            material = material,
            deadline = deadline,
            price = price,
            description = description,
            version = version
    )
}

fun Analysis.toEntity(): AnalysisEntity {
    return AnalysisEntity(
            id = id,
            code = code,
            name = name,
            material = material,
            deadline = deadline,
            price = price,
            description = description,
            version = version,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
    )
}

fun AnalyticsAnalysis.toEntity(): AnalyticsAnalysisEntity {
    return AnalyticsAnalysisEntity(
            id = id,
            analysisId = analysisId,
            cityId = cityId,
            salesCount = salesCount,
            viewsCount = viewsCount,
            cartCount = cartCount,
            lastSaleDate = lastSaleDate,
            version = version,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
    )
}

fun AnalyticsAnalysisEntity.toModel(): AnalyticsAnalysis {
    return AnalyticsAnalysis(
            id = id,
            analysisId = analysisId,
            cityId = cityId,
            salesCount = salesCount,
            viewsCount = viewsCount,
            cartCount = cartCount
    )
}

fun Cart.toEntity(): CartEntity {
    return CartEntity(
            id = id,
            userId = userId,
            analysisId = analysisId,
            version = null,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
    )
}

fun CartEntity.toModel(): Cart {
    return Cart(
            id = id,
            userId = userId,
            analysisId = analysisId
    )
}

fun CityEntity.toModel(): City {
    return City(
            id = id,
            name = name
    )
}

fun City.toEntity(): CityEntity {
    return CityEntity(
            id = id,
            name = name,
            version = null,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
    )
}