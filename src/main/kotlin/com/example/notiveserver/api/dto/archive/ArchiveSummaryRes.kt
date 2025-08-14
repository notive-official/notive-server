package com.example.notiveserver.api.dto.archive

import com.example.notiveserver.application.archive.dto.ArchiveSummaryDto
import com.example.notiveserver.application.user.dto.UserSummaryDto
import com.example.notiveserver.common.enums.ArchiveType
import java.util.*

data class ArchiveSummaryRes(
    val id: UUID,
    val thumbnailPath: String,
    val tags: List<String>,
    val isPublic: Boolean,
    val type: ArchiveType,
    val isReplicable: Boolean,
    val title: String,
    val summary: String,
    val writer: WriterSummaryRes
) {
    companion object {
        fun of(
            archive: ArchiveSummaryDto,
            tags: List<String>,
            writer: UserSummaryDto
        ): ArchiveSummaryRes =
            ArchiveSummaryRes(
                id = archive.id,
                thumbnailPath = archive.thumbnailPath.filePath,
                tags = tags,
                title = archive.title,
                isPublic = archive.isPublic,
                type = archive.type,
                isReplicable = archive.isReplicable,
                summary = archive.summary,
                writer = WriterSummaryRes(
                    id = writer.id,
                    nickname = writer.nickname,
                    profileImagePath = writer.profileImage.filePath
                )
            )
    }
}