package com.example.notiveserver.application.archive.dto

import com.example.notiveserver.application.user.dto.UserSummaryDto
import java.util.*

data class ArchiveSummaryDto(
    val id: UUID,
    val title: String,
    val thumbnailPath: ArchiveThumbnailDto,
    val writer: UserSummaryDto
)
