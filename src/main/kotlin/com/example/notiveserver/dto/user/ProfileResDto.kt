package com.example.notiveserver.dto.user

data class ProfileResDto(
    val name: String,
    val nickname: String,
    val email: String,
    val profileImageUrl: String?
)