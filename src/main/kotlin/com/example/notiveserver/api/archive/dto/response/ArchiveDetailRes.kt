package com.example.notiveserver.api.archive.dto.response

import com.example.notiveserver.application.archive.dto.ArchiveBlockDto
import com.example.notiveserver.application.archive.dto.ArchiveMetaDto

data class ArchiveDetailRes(
    val meta: ArchiveMetaRes,
    val canEdit: Boolean,
    val canDelete: Boolean,
    val canDuplicate: Boolean,
    val isMarked: Boolean,
    val tags: List<String>,
    val blocks: List<BlockRes>,
) {
    companion object {
        fun of(
            meta: ArchiveMetaDto,
            canEdit: Boolean,
            canDelete: Boolean,
            canDuplicate: Boolean,
            tags: List<String>,
            isMarked: Boolean,
            blocks: List<ArchiveBlockDto>
        ): ArchiveDetailRes {
            return ArchiveDetailRes(
                meta = ArchiveMetaRes.of(meta, meta.writer, meta.group),
                canEdit = canEdit,
                canDelete = canDelete,
                canDuplicate = canDuplicate,
                isMarked = isMarked,
                tags = tags,
                blocks = blocks.map { BlockRes.of(it) }
            )
        }
    }
}
