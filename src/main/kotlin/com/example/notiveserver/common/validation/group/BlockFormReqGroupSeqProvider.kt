package com.example.notiveserver.common.validation.group

import com.example.notiveserver.api.dto.archive.BlockFormReq
import com.example.notiveserver.common.enums.BlockType
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider

class BlockFormReqGroupSeqProvider : DefaultGroupSequenceProvider<BlockFormReq> {
    override fun getValidationGroups(value: BlockFormReq?): MutableList<Class<*>> {
        val groups = mutableListOf<Class<*>>(BlockFormReq::class.java) // 항상 자신(=Default)
        when (value?.type) {
            BlockType.PARAGRAPH -> groups += TextGroup::class.java
            BlockType.H1 -> groups += TextGroup::class.java
            BlockType.H2 -> groups += TextGroup::class.java
            BlockType.H3 -> groups += TextGroup::class.java
            BlockType.IMAGE -> groups += ImageGroup::class.java
            BlockType.LINK -> groups += LinkGroup::class.java
            else -> {}
        }
        return groups
    }
}