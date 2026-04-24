package com.example.notiveserver.api.dto

import java.time.LocalDateTime

data class ErrorRes(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String? = null,
)
