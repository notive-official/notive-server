package com.example.notiveserver.application.archive.dto

import com.example.notiveserver.common.enums.BlockType
import com.example.notiveserver.domain.model.archive.Archive
import com.example.notiveserver.domain.model.archive.ArchiveBlock

data class BlockInfoDto(
    val id: Long? = null,
    val position: Int,
    val type: BlockType,
    val payload: PayloadDto
) {
    fun createArchiveBlock(payload: String, archive: Archive): ArchiveBlock = ArchiveBlock.create(
        position = position,
        type = type,
        payload = payload,
        archive = archive
    )
}