package com.example.notiveserver.api.dto

import org.springframework.data.domain.Page

data class SliceMeta(
    val page: Int,          // 0‑based
    val size: Int,
    val hasNext: Boolean,
    val hasPrev: Boolean,
) {
    companion object {
        fun <T> of(page: Page<T>): SliceMeta {
            return SliceMeta(
                page = page.pageable.pageNumber,
                size = page.size,
                hasPrev = !page.isFirst,
                hasNext = !page.isLast,
            )
        }
    }
}