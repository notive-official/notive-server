package com.example.notiveserver.application.user.dto

import java.util.*

data class UserSummaryDto(
    val id: UUID,
    val name: String,
    val nickname: String,
    val email: String,
    val profileImage: ProfileImageDto,
)