package com.example.notiveserver.domain.model.archive

import com.example.notiveserver.common.enums.BlockType
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("LINK")
class LinkBlock(
    id: Long? = null,
    position: Int,
    type: BlockType,
    archive: Archive,

    @Column(name = "url", nullable = true, length = 255)
    val url: String
) : ArchiveBlock(id = id, position = position, type = type, archive = archive)