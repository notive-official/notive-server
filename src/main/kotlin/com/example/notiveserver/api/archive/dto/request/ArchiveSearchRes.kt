package com.example.notiveserver.api.archive.dto.request

import com.example.notiveserver.api.archive.dto.response.ArchiveSummaryRes
import com.example.notiveserver.api.dto.SliceRes

data class ArchiveSearchRes(
    val tags: List<String>,
    val archives: SliceRes<ArchiveSummaryRes>
)