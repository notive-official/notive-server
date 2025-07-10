package com.example.notiveserver.api.dto.archive

data class OEmbedEndpoint(
    val schemes: List<String>?,
    val url: String,
    val formats: List<String>?
)