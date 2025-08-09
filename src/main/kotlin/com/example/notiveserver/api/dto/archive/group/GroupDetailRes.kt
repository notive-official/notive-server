package com.example.notiveserver.api.dto.archive.group

import java.util.*

data class GroupDetailRes(
    val id: UUID,
    val name: String,
    val thumbnails: List<String>,
    val totalElements: Long
)