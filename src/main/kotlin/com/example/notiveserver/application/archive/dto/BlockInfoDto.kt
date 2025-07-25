package com.example.notiveserver.application.archive.dto

import com.example.notiveserver.common.enums.BlockType

data class BlockInfoDto(
    val position: Int,
    val blockType: BlockType,
    val payload: String
)