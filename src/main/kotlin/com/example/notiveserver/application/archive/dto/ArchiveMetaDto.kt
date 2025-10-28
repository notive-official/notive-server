package com.example.notiveserver.application.archive.dto

import com.example.notiveserver.application.user.dto.UserSummaryDto
import com.example.notiveserver.common.enums.ArchiveType
import com.example.notiveserver.domain.model.archive.Archive
import com.example.notiveserver.domain.model.archive.Group
import com.example.notiveserver.domain.model.user.User
import java.util.*

data class ArchiveMetaDto(
    val id: UUID,
    val title: String,
    val thumbnailPath: String?,
    val isPublic: Boolean,
    val isDuplicable: Boolean,
    val type: ArchiveType,
    val writer: UserSummaryDto,
    val group: GroupSummaryDto,
) {
    companion object {
        fun of(archive: Archive, writer: User, group: Group): ArchiveMetaDto =
            ArchiveMetaDto(
                id = requireNotNull(archive.id),
                title = archive.title,
                thumbnailPath = archive.thumbnailPath,
                type = archive.type,
                isPublic = archive.isPublic,
                isDuplicable = archive.isDuplicable,
                group = GroupSummaryDto(id = requireNotNull(group.id), name = group.name),
                writer = UserSummaryDto(
                    id = requireNotNull(writer.id),
                    nickname = writer.nickname,
                    profileImagePath = writer.profileImage
                )
            )
    }
}
