package innobiz.crm.genompluslab.feature.repositories

import innobiz.crm.genompluslab.feature.files.data.FileEntity
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRepository: CoroutineCrudRepository<FileEntity, String> {
}