package innobiz.crm.genompluslab.feature.files.presentation.dto

import innobiz.crm.genompluslab.feature.files.domain.models.FileDirectory
import org.springframework.http.codec.multipart.FilePart

data class CreateFileDto(
        val directory: FileDirectory,
        val part: FilePart
)
