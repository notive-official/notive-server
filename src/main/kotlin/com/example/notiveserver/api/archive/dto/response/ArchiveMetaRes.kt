package com.example.notiveserver.api.archive.dto.response

import com.example.notiveserver.api.group.dto.GroupSummaryRes
import com.example.notiveserver.application.archive.dto.ArchiveMetaDto
import com.example.notiveserver.application.group.dto.GroupSummaryDto
import com.example.notiveserver.application.user.dto.UserSummaryDto
import com.example.notiveserver.common.enums.ArchiveType
import java.util.*

data class ArchiveMetaRes(
    val id: UUID,
    val thumbnailPath: String?,
    val title: String,
    val isPublic: Boolean,
    val type: ArchiveType,
    val isDuplicable: Boolean,
    val writer: WriterSummaryRes,
    val group: GroupSummaryRes
) {
    companion object {
        fun of(
            archive: ArchiveMetaDto,
            writer: UserSummaryDto,
            group: GroupSummaryDto
        ): ArchiveMetaRes =
            ArchiveMetaRes(
                id = archive.id,
                title = archive.title,
                thumbnailPath = archive.thumbnailPath,
                isPublic = archive.isPublic,
                type = archive.type,
                isDuplicable = archive.isDuplicable,
                group = GroupSummaryRes(id = group.id, name = group.name),
                writer = WriterSummaryRes(
                    id = writer.id,
                    nickname = writer.nickname,
                    profileImagePath = writer.profileImagePath
                )
            )
    }
}