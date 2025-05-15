package com.example.notiveserver.dto.oembed

data class OEmbedEndpoint(
    val schemes: List<String>?,
    val url: String,
    val formats: List<String>?
)