package com.example.notiveserver.application.archive.dto

import com.example.notiveserver.common.policy.DefaultImagePath

data class ArchiveThumbnailDto(
    val filePath: String = DefaultImagePath.ARCHIVE_THUMBNAIL
) {
    companion object {
        fun of(path: String?): ArchiveThumbnailDto =
            ArchiveThumbnailDto(path ?: DefaultImagePath.ARCHIVE_THUMBNAIL)
    }
}