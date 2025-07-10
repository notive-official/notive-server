package com.example.notiveserver.api.dto.archive

data class CreateArchiveReq(
    val title: String,
    val tags: List<String>,
    val elements: String,
)