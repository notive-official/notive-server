package com.example.notiveserver.application.oembed.dto

data class OEmbedEndpointDto(
    val schemes: List<String>?,
    val url: String,
    val formats: List<String>?
)