package com.example.notiveserver.application.archive.dto

import com.example.notiveserver.application.user.dto.ProfileImageDto
import com.example.notiveserver.application.user.dto.UserSummaryDto
import com.example.notiveserver.common.enums.ArchiveType
import com.example.notiveserver.domain.model.archive.Archive
import com.example.notiveserver.domain.model.user.User
import java.util.*

data class ArchiveSummaryDto(
    val id: UUID,
    val title: String,
    val thumbnailPath: ArchiveThumbnailDto,
    val isPublic: Boolean,
    val type: ArchiveType,
    val isDuplicable: Boolean,
    val summary: String,
    val writer: UserSummaryDto
) {
    companion object {
        fun of(archive: Archive, writer: User): ArchiveSummaryDto =
            ArchiveSummaryDto(
                id = requireNotNull(archive.id),
                title = archive.title,
                thumbnailPath = ArchiveThumbnailDto.of(archive.thumbnailPath),
                type = archive.type,
                isPublic = archive.isPublic,
                isDuplicable = archive.isDuplicable,
                summary = archive.summary,
                writer = UserSummaryDto(
                    id = requireNotNull(writer.id),
                    nickname = writer.nickname,
                    profileImage = ProfileImageDto.of(writer.profileImage)
                )
            )
    }
}
