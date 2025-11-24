package com.example.notiveserver.application.archive.dto

import com.example.notiveserver.domain.archive.model.Archive
import com.example.notiveserver.domain.archive.model.ArchiveBlock
import com.example.notiveserver.domain.group.model.Group
import com.example.notiveserver.domain.user.model.User

data class ArchiveDetailDto(
    val meta: ArchiveMetaDto,
    val blocks: List<ArchiveBlockDto>
) {
    companion object {
        fun of(
            archive: Archive,
            writer: User,
            group: Group,
            blocks: List<ArchiveBlock>
        ): ArchiveDetailDto =
            ArchiveDetailDto(
                meta = ArchiveMetaDto.of(archive, writer, group),
                blocks = blocks.map { ArchiveBlockDto.of(it) }
            )
    }
}