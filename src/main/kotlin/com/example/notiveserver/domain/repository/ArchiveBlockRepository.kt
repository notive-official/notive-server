package com.example.notiveserver.domain.repository

import com.example.notiveserver.domain.model.archive.ArchiveBlock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ArchiveBlockRepository : JpaRepository<ArchiveBlock, Long> {
    fun findAllByArchiveId(archiveId: UUID): List<ArchiveBlock>
}
