package com.example.notiveserver.api.dto.common

import org.springframework.data.domain.Page

data class PageMeta(
    val page: Int,          // 0‑based
    val size: Int,
    val totalPages: Int,
    val totalElements: Long,
    val hasNext: Boolean,
    val hasPrev: Boolean,
) {
    companion object {
        fun <T> of(page: Page<T>): PageMeta {
            return PageMeta(
                page = page.pageable.pageNumber,
                size = page.size,
                totalPages = page.totalPages,
                totalElements = page.totalElements,
                hasPrev = !page.isFirst,
                hasNext = !page.isLast,
            )
        }
    }
}