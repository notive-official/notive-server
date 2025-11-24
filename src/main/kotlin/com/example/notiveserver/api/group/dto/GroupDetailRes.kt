package com.example.notiveserver.api.group.dto

import com.example.notiveserver.application.group.dto.GroupDetailDto
import java.util.*

data class GroupDetailRes(
    val id: UUID,
    val name: String,
    val thumbnails: List<String?>,
    val totalElements: Long
) {
    companion object {
        fun of(group: GroupDetailDto): GroupDetailRes = GroupDetailRes(
            id = group.id,
            name = group.name,
            thumbnails = group.thumbnails,
            totalElements = group.totalElements
        )
    }
}