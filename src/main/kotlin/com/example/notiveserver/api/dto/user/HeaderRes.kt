package com.example.notiveserver.api.dto.user

import java.util.*

data class HeaderRes(
    val userId: UUID,
    val nickname: String,
    val profileImageUrl: String
)