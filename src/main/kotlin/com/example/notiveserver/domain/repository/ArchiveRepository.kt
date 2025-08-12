package com.example.notiveserver.domain.repository

import com.example.notiveserver.domain.model.archive.Archive
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ArchiveRepository : JpaRepository<Archive, UUID> {
    fun findTop3ByGroupIdOrderByCreatedAtDesc(groupId: UUID): List<Archive>
    fun countByGroupId(groupId: UUID): Long
    fun findByWriterIdOrderByCreatedAtDesc(userId: UUID, pageable: Pageable): Page<Archive>
    fun findByIsPublicTrueOrderByCreatedAtDesc(pageable: Pageable): Page<Archive>
}
