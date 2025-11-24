package com.example.notiveserver.api.dto

data class SliceRes<T>(
    val meta: SliceMeta,
    val content: List<T>,
)