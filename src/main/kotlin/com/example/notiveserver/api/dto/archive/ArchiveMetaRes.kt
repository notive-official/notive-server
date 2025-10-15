package com.example.notiveserver.api.dto.archive

import com.example.notiveserver.application.archive.dto.ArchiveSummaryDto
import com.example.notiveserver.application.user.dto.UserSummaryDto
import com.example.notiveserver.common.enums.ArchiveType
import java.util.*

data class ArchiveMetaRes(
    val id: UUID,
    val thumbnailPath: String,
    val title: String,
    val isPublic: Boolean,
    val type: ArchiveType,
    val isDuplicable: Boolean,
    val writer: WriterSummaryRes
) {
    companion object {
        fun of(
            archive: ArchiveSummaryDto,
            writer: UserSummaryDto
        ): ArchiveMetaRes =
            ArchiveMetaRes(
                id = archive.id,
                title = archive.title,
                thumbnailPath = archive.thumbnailPath.filePath,
                isPublic = archive.isPublic,
                type = archive.type,
                isDuplicable = archive.isDuplicable,
                writer = WriterSummaryRes(
                    id = writer.id,
                    nickname = writer.nickname,
                    profileImagePath = writer.profileImage.filePath
                )
            )
    }
}