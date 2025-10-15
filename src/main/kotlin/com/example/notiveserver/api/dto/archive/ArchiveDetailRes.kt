package com.example.notiveserver.api.dto.archive

import com.example.notiveserver.application.archive.dto.ArchiveBlockDto
import com.example.notiveserver.application.archive.dto.ArchiveSummaryDto

data class ArchiveDetailRes(
    val meta: ArchiveMetaRes,
    val canEdit: Boolean,
    val isMarked: Boolean,
    val tags: List<String>,
    val blocks: List<BlockRes>,
) {
    companion object {
        fun of(
            meta: ArchiveSummaryDto,
            canEdit: Boolean,
            tags: List<String>,
            isMarked: Boolean,
            blocks: List<ArchiveBlockDto>
        ): ArchiveDetailRes {
            return ArchiveDetailRes(
                meta = ArchiveMetaRes.of(meta, meta.writer),
                canEdit = canEdit,
                isMarked = isMarked,
                tags = tags,
                blocks = blocks.map { BlockRes.of(it) }
            )
        }
    }
}
