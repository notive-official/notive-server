package com.example.notiveserver.api.dto.common

data class PageMeta(
    val page: Int,          // 0‑based
    val size: Int,
    val totalPages: Int,
    val totalElements: Long,
    val hasNext: Boolean,
    val hasPrev: Boolean,
)