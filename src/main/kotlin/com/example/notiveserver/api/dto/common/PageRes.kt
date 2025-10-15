package com.example.notiveserver.api.dto.common

data class PageRes<T>(
    val meta: PageMeta,
    val content: List<T>,
)