package innobiz.crm.genompluslab.feature.files.domain.usecases

import innobiz.crm.genompluslab.feature.files.domain.models.File
import innobiz.crm.genompluslab.feature.files.domain.models.FileDirectory
import innobiz.crm.genompluslab.feature.files.domain.services.FileService
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service

interface UploadFileUseCase {
    suspend operator fun invoke(directory: FileDirectory, part: FilePart): File
}

@Service
internal class UploadFileUseCaseImpl(
        private val fileService: FileService
): UploadFileUseCase {
    override suspend fun invoke(directory: FileDirectory, part: FilePart): File {
        return fileService.storeFile(directory, part)
    }

}