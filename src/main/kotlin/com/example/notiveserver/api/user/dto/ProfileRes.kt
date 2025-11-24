package com.example.notiveserver.api.user.dto

data class ProfileRes(
    val name: String,
    val nickname: String,
    val email: String,
    val profileImagePath: String?
)