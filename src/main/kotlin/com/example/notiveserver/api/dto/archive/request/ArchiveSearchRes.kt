package com.example.notiveserver.api.dto.archive.request

import com.example.notiveserver.api.dto.archive.response.ArchiveSummaryRes
import com.example.notiveserver.api.dto.common.SliceRes

data class ArchiveSearchRes(
    val tags: List<String>,
    val archives: SliceRes<ArchiveSummaryRes>
)