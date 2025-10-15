package com.example.notiveserver.api.dto.user

data class ProfileRes(
    val name: String,
    val nickname: String,
    val email: String,
    val profileImagePath: String
)