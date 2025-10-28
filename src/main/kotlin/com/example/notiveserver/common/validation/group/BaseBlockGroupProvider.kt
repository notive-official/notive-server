package com.example.notiveserver.common.validation.group

import com.example.notiveserver.common.enums.BlockType
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider

interface HasBlockType {
    val type: BlockType?
}

abstract class BaseBlockGroupProvider<T : HasBlockType>(
    private val root: Class<T>
) : DefaultGroupSequenceProvider<T> {
    override fun getValidationGroups(value: T?): MutableList<Class<*>> {
        val groups = mutableListOf<Class<*>>(root)
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