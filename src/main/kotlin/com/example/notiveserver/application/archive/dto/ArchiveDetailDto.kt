package com.example.notiveserver.application.archive.dto

import com.example.notiveserver.domain.model.archive.Archive
import com.example.notiveserver.domain.model.archive.ArchiveBlock
import com.example.notiveserver.domain.model.user.User

data class ArchiveDetailDto(
    val meta: ArchiveSummaryDto,
    val blocks: List<ArchiveBlockDto>
) {
    companion object {
        fun of(archive: Archive, writer: User, blocks: List<ArchiveBlock>): ArchiveDetailDto =
            ArchiveDetailDto(
                meta = ArchiveSummaryDto.of(archive, writer),
                blocks = blocks.map { ArchiveBlockDto.of(it) }
            )
    }
}