package com.example.notiveserver.api.dto

data class PageRes<T>(
    val meta: PageMeta,
    val content: List<T>,
)