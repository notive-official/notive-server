package com.example.notiveserver.infrastructure.security

import com.example.notiveserver.domain.repository.ArchiveRepository
import com.example.notiveserver.domain.repository.GroupRepository
import org.springframework.stereotype.Component
import java.util.*

@Component("ownershipSecurity")
class OwnershipSecurity(
    private val archiveRepository: ArchiveRepository,
    private val groupRepository: GroupRepository,
) {
    /**
     * @param archiveId 조회할 문서 UUID
     * @param userId 현재 인증된 사용자의 UUID
     */
    fun isArchiveOwner(archiveId: UUID, userId: UUID): Boolean =
        archiveRepository.findById(archiveId)
            .map { it.writer.id == userId }
            .orElse(false)

    /**
     * @param groupId 조회할 그룹 UUID
     * @param userId 현재 인증된 사용자의 UUID
     */
    fun isGroupOwner(groupId: UUID, userId: UUID): Boolean =
        groupRepository.findById(groupId)
            .map { it.user.id == userId }
            .orElse(false)
}