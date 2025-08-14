package com.example.notiveserver.api.dto.archive

import com.example.notiveserver.application.archive.dto.ArchiveBlockDto
import com.example.notiveserver.common.enums.BlockType

data class BlockRes(
    val id: Long,
    val type: BlockType,
    val position: Int,
    val payload: String,
) {
    companion object {
        fun of(block: ArchiveBlockDto) = BlockRes(
            id = block.id,
            type = block.type,
            position = block.position,
            payload = block.payload,
        )
    }
}
