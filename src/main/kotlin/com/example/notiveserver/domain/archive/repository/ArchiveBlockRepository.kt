package com.example.notiveserver.domain.archive.repository

import com.example.notiveserver.domain.archive.model.ArchiveBlock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ArchiveBlockRepository : JpaRepository<ArchiveBlock, Long> {
    fun findAllByArchiveId(archiveId: UUID): List<ArchiveBlock>
    fun deleteByArchiveId(archiveId: UUID)
}
