package com.example.notiveserver.api.dto.archive

import com.example.notiveserver.application.archive.dto.ArchiveBlockDto
import com.example.notiveserver.application.archive.dto.ArchiveSummaryDto

data class ArchiveDetailRes(
    val meta: ArchiveMetaRes,
    val tags: List<String>,
    val blocks: List<BlockRes>,
) {
    companion object {
        fun of(
            meta: ArchiveSummaryDto,
            tags: List<String>,
            blocks: List<ArchiveBlockDto>
        ): ArchiveDetailRes {
            return ArchiveDetailRes(
                meta = ArchiveMetaRes.of(meta, meta.writer),
                tags = tags,
                blocks = blocks.map { BlockRes.of(it) }
            )
        }
    }
}
