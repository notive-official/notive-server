package com.example.notiveserver.api.dto.my.archive

import java.util.*

data class ArchiveSummaryRes(
    val id: UUID,
    val thumbnailPath: String,
    val title: String,
    val writer: WriterSummaryRes
)