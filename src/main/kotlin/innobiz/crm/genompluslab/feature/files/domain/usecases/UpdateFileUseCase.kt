package com.dev.course.feature.files.domain.usecases

import com.dev.course.feature.files.domain.models.File
import com.dev.course.feature.files.domain.models.FileDirectory
import com.dev.course.feature.files.domain.services.FileService
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service

interface UpdateFileUseCase {
    suspend operator fun invoke(id: String, directory: FileDirectory?, part: FilePart?): File
}

@Service
internal class UpdateFileUseCaseImpl(
        private val fileService: FileService
): UpdateFileUseCase {
    override suspend fun invoke(id: String, directory: FileDirectory?, part: FilePart?): File {
        return fileService.updateFile(id, directory, part)
    }

}