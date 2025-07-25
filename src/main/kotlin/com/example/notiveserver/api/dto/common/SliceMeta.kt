package com.example.notiveserver.api.dto.common

data class SliceMeta(
    val page: Int,          // 0‑based
    val size: Int,
    val hasNext: Boolean,
    val hasPrev: Boolean,
)