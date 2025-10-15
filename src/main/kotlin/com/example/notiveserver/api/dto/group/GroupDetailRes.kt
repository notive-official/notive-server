package com.example.notiveserver.api.dto.group

import com.example.notiveserver.application.archive.dto.GroupDetailDto
import java.util.*

data class GroupDetailRes(
    val id: UUID,
    val name: String,
    val thumbnails: List<String>,
    val totalElements: Long
) {
    companion object {
        fun of(group: GroupDetailDto): GroupDetailRes = GroupDetailRes(
            id = group.id,
            name = group.name,
            thumbnails = group.thumbnails.map { it.filePath },
            totalElements = group.totalElements
        )
    }
}