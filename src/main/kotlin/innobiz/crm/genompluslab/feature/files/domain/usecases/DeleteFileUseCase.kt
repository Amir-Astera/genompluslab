package com.dev.course.feature.files.domain.usecases

import com.dev.course.feature.files.domain.models.FileDirectory
import com.dev.course.feature.files.domain.services.FileService
import org.springframework.stereotype.Service

interface DeleteFileUseCase {
    suspend operator fun invoke(id: String, directory: FileDirectory, format: String): Unit
}

@Service
internal class DeleteFileUseCaseImpl(
        private val fileService: FileService
): DeleteFileUseCase {
    override suspend fun invoke(id: String, directory: FileDirectory, format: String) {
        return fileService.deleteFile(id, directory, format)
    }

}