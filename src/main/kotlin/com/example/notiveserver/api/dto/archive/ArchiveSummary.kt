package com.example.notiveserver.api.dto.archive

import java.util.*

data class ArchiveSummary(
    val id: UUID,
    val thumbnailPath: String,
    val title: String,
    val writer: WriterSummary
)