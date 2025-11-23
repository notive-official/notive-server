package com.example.notiveserver.infrastructure.security

import com.example.notiveserver.common.exception.ArchiveException
import com.example.notiveserver.common.exception.code.ArchiveErrorCode
import com.example.notiveserver.domain.archive.repository.ArchiveBlockRepository
import com.example.notiveserver.domain.archive.repository.ArchiveRepository
import com.example.notiveserver.domain.group.repository.GroupRepository
import org.springframework.stereotype.Component
import java.util.*

@Component("accessManager")
class AccessManager(
    private val archiveRepository: ArchiveRepository,
    private val archiveBlockRepository: ArchiveBlockRepository,
    private val groupRepository: GroupRepository,
    private val currentUser: SecurityCurrentUserProvider
) {
    /**
     * @param archiveId 조회할 문서 UUID
     */
    fun isArchiveOwner(archiveId: UUID): Boolean {
        val accessible = archiveRepository.findById(archiveId)
        if (accessible.isEmpty) {
            throw ArchiveException(ArchiveErrorCode.ARCHIVE_NOT_FOUND)
        }
        if (accessible.get().writer.id != currentUser.id()) {
            throw ArchiveException(ArchiveErrorCode.NOT_ARCHIVE_OWNER)
        }
        return true
    }

    /**
     * @param archiveId 조회할 문서 UUID
     */
    fun isBelongsToArchive(archiveId: UUID, blockIds: List<Long>): Boolean {
        val accessible = archiveRepository.findById(archiveId)
        if (accessible.isEmpty) {
            throw ArchiveException(ArchiveErrorCode.ARCHIVE_NOT_FOUND)
        }
        val existBlockIds = archiveBlockRepository.findAllByArchiveId(archiveId).map { it.id }
        val notValidIds = blockIds - existBlockIds.toSet()
        return notValidIds.isEmpty()
    }

    /**
     * @param archiveId 조회할 문서 UUID
     */
    fun canReadArchive(archiveId: UUID): Boolean =
        archiveRepository.findById(archiveId)
            .map { it.isPublic || it.writer.id == currentUser.id() }
            .orElse(false)

    /**
     * @param groupId 조회할 그룹 UUID
     */
    fun isGroupOwner(groupId: UUID): Boolean {
        val accessible = groupRepository.findById(groupId)
        if (accessible.isEmpty) {
            throw ArchiveException(ArchiveErrorCode.GROUP_NOT_FOUND)
        }
        if (accessible.get().user.id != currentUser.id()) {
            throw ArchiveException(ArchiveErrorCode.NOT_GROUP_OWNER)
        }
        return true
    }
}