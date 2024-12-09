package com.dev.course.feature.files.data

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "file_module")
data class ModuleFilesEntity(
        @Id
        val id: String,
        val fileId: String,
        val moduleId: String,
        @Version
        val version: Long?,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime
)
