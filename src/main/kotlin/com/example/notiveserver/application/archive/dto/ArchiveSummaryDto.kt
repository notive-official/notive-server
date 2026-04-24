package com.example.notiveserver.application.archive.dto

import com.example.notiveserver.application.user.dto.UserSummaryDto
import com.example.notiveserver.common.enums.ArchiveType
import com.example.notiveserver.domain.archive.model.Archive
import com.example.notiveserver.domain.user.model.User
import java.util.*

data class ArchiveSummaryDto(
    val id: UUID,
    val title: String,
    val thumbnailPath: String?,
    val isPublic: Boolean,
    val type: ArchiveType,
    val summary: String,
    val writer: UserSummaryDto
) {
    companion object {
        fun of(archive: Archive, writer: User): ArchiveSummaryDto =
            ArchiveSummaryDto(
                id = requireNotNull(archive.id),
                title = archive.title,
                thumbnailPath = archive.thumbnailPath,
                type = archive.type,
                isPublic = archive.isPublic,
                summary = archive.summary,
                writer = UserSummaryDto(
                    id = requireNotNull(writer.id),
                    nickname = writer.nickname,
                    profileImagePath = writer.profileImage
                )
            )
    }
}
