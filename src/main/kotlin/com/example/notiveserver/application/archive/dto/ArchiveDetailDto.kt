package com.example.notiveserver.application.archive.dto

import com.example.notiveserver.domain.model.archive.Archive
import com.example.notiveserver.domain.model.archive.ArchiveBlock
import com.example.notiveserver.domain.model.archive.Group
import com.example.notiveserver.domain.model.user.User

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