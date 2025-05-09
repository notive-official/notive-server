package com.example.notiveserver.dto.user

data class HeaderResDto(
    val userId: Long,
    val nickname: String,
    val profileImageUrl: String?
)