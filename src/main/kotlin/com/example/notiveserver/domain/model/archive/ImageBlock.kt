package com.example.notiveserver.domain.model.archive

import com.example.notiveserver.common.enums.BlockType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("IMAGE")
class ImageBlock(
    id: Long? = null,
    position: Int,
    type: BlockType,
    archive: Archive,

    @Column(name = "path", nullable = true, length = 255)
    val path: String
) : ArchiveBlock(id = id, position = position, type = type, archive = archive)