package com.example.notiveserver.infrastructure.security

import com.example.notiveserver.domain.repository.ArchiveRepository
import com.example.notiveserver.domain.repository.GroupRepository
import org.springframework.stereotype.Component
import java.util.*

@Component("accessManager")
class AccessManager(
    private val archiveRepository: ArchiveRepository,
    private val groupRepository: GroupRepository,
) {
    /**
     * @param archiveId 조회할 문서 UUID
     */
    fun isArchiveOwner(archiveId: UUID): Boolean =
        archiveRepository.findById(archiveId)
            .map { it.writer.id == SecurityUtils.currentUserId }
            .orElse(false)

    /**
     * @param archiveId 조회할 문서 UUID
     */
    fun canReadArchive(archiveId: UUID): Boolean =
        archiveRepository.findById(archiveId)
            .map { it.isPublic || it.writer.id == SecurityUtils.currentUserId }
            .orElse(false)

    /**
     * @param groupId 조회할 그룹 UUID
     */
    fun isGroupOwner(groupId: UUID): Boolean =
        groupRepository.findById(groupId)
            .map { it.user.id == SecurityUtils.currentUserId }
            .orElse(false)
}