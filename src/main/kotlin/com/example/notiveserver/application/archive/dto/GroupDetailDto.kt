package com.example.notiveserver.application.archive.dto

import jakarta.validation.constraints.Size
import java.util.*

data class GroupDetailDto(
    val id: UUID,
    val name: String,
    @Size(min = 3, max = 3)
    val thumbnails: List<ArchiveThumbnailDto>,
    val totalElements: Long
)