package com.example.notiveserver.api.dto.user

data class HeaderRes(
    val userId: Long,
    val nickname: String,
    val profileImageUrl: String
)