package com.dev.course.feature.files.presentation.dto

import com.dev.course.feature.files.domain.models.FileDirectory
import org.springframework.http.codec.multipart.FilePart

data class UpdateFileDto(
        val directory: FileDirectory?,
        val part: FilePart?
)
