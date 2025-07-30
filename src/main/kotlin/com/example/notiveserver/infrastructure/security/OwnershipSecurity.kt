package com.example.notiveserver.infrastructure.security

import com.example.notiveserver.domain.repository.ArchiveRepository
import org.springframework.stereotype.Component
import java.util.*

@Component("ownershipSecurity")
class OwnershipSecurity(
    private val archiveRepository: ArchiveRepository
) {
    /**
     * @param archiveId 조회할 문서 UUID
     * @param userId 현재 인증된 사용자의 UUID
     */
    fun isArchiveOwner(archiveId: UUID, userId: UUID): Boolean =
        archiveRepository.findById(archiveId)
            .map { it.writer.id == userId }
            .orElse(false)
}