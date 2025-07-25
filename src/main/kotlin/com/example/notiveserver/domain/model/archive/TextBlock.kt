package com.example.notiveserver.domain.model.archive

import com.example.notiveserver.common.enums.BlockType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("TEXT")
class TextBlock(
    id: Long? = null,
    position: Int,
    type: BlockType,
    archive: Archive,

    @Column(name = "content", nullable = true, columnDefinition = "TEXT")
    val payload: String
) : ArchiveBlock(id = id, position = position, type = type, archive = archive)