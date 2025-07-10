package com.example.notiveserver.api.dto.common

import java.time.LocalDateTime

data class ErrorRes(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String? = null,
)
