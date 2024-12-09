package com.dev.course.feature.files.data

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "file_lesson")
data class LessonFilesEntity(
        @Id
        val id: String,
        val fileId: String,
        val lessonId: String,
        @Version
        val version: Long?,
        val createdAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime
)