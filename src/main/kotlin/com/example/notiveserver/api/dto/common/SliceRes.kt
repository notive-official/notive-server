package com.example.notiveserver.api.dto.common

data class SliceRes<T>(
    val meta: SliceMeta,
    val content: List<T>,
)