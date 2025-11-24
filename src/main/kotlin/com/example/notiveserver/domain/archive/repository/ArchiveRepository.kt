package com.example.notiveserver.domain.archive.repository

import com.example.notiveserver.common.enums.ArchiveType
import com.example.notiveserver.domain.archive.model.Archive
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ArchiveRepository : JpaRepository<Archive, UUID>, ArchiveRepositoryCustom {
    fun findTop3ByGroupIdOrderByCreatedAtDesc(groupId: UUID): List<Archive>
    fun countByGroupId(groupId: UUID): Long
    fun findByGroupId(groupId: UUID, pageable: Pageable): Page<Archive>
    fun findByGroupId(groupId: UUID): List<Archive>
    fun findByWriterId(
        userId: UUID,
        pageable: Pageable
    ): Page<Archive>

    fun findByWriterIdAndType(
        userId: UUID,
        type: ArchiveType,
        pageable: Pageable
    ): Page<Archive>
}
