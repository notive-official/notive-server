package com.example.notiveserver.application.group.dto

import jakarta.validation.constraints.Size
import java.util.*

data class GroupDetailDto(
    val id: UUID,
    val name: String,
    @Size(min = 3, max = 3)
    val thumbnails: List<String?>,
    val totalElements: Long
)