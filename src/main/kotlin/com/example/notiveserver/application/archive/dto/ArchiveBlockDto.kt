package com.example.notiveserver.application.archive.dto

import com.example.notiveserver.common.enums.BlockType
import com.example.notiveserver.domain.model.archive.ArchiveBlock

data class ArchiveBlockDto(
    val id: Long,
    val type: BlockType,
    val position: Int,
    val payload: String,
) {
    companion object {
        fun of(block: ArchiveBlock): ArchiveBlockDto = ArchiveBlockDto(
            id = block.id!!,
            type = block.type,
            position = block.position,
            payload = block.payload!!
        )
    }
}