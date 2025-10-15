package com.example.notiveserver.application.user.dto

import com.example.notiveserver.common.policy.DefaultImagePath

data class ProfileImageDto(
    val filePath: String
) {
    companion object {
        fun of(path: String?): ProfileImageDto =
            ProfileImageDto(path ?: DefaultImagePath.PROFILE)
    }
}