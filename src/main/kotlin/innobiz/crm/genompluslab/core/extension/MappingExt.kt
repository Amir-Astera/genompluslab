package innobiz.crm.genompluslab.core.extension

import com.dev.course.feature.files.data.FileEntity
import com.dev.course.feature.files.domain.models.File
import innobiz.crm.genompluslab.feature.users.data.entity.UserEntity
import com.dev.course.feature.users.domain.models.UserAggregate
import innobiz.crm.genompluslab.feature.topic.data.TopicEntity
import innobiz.crm.genompluslab.feature.topic.domain.models.Topic
import java.time.LocalDateTime

//fun UserEntity.toModel(
//        authorities: Collection<Authority>,
//
//): UserAggregate {
//    return UserAggregate(
//            id = id,
//            name = name,
//            surname = surname,
//            email = email,
//            phone = phone,
//            login = login,
//            authorities = authorities,
//            logoUrl = logo,
//            version = version,
//            createdAt = createdAt,
//            updatedAt = updatedAt
//    )
//}
//
//fun AuthorityEntity.toModel(
//        version: Long? = null,
//        createdAt: LocalDateTime? = null
//): Authority {
//    return Authority(
//            id = id,
//            name = name,
//            description = description,
//            version = version ?: this.version,
//            createdAt = createdAt ?: this.createdAt,
//            updatedAt = updatedAt
//    )
//}
//
//fun Authority.toEntity(): AuthorityEntity {
//    return AuthorityEntity(
//            id = id,
//            name = name,
//            description = description,
//            version = version,
//            createdAt = createdAt,
//            updatedAt = updatedAt
//    )
//}

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