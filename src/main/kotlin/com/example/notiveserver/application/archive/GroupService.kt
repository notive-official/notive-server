package com.example.notiveserver.application.archive

import com.example.notiveserver.application.archive.dto.GroupDetailDto
import com.example.notiveserver.application.archive.dto.GroupSummaryDto
import com.example.notiveserver.common.exception.ArchiveException
import com.example.notiveserver.common.exception.code.ArchiveErrorCode
import com.example.notiveserver.domain.group.model.Group
import com.example.notiveserver.domain.archive.repository.ArchiveRepository
import com.example.notiveserver.domain.group.repository.GroupRepository
import com.example.notiveserver.domain.user.repository.UserRepository
import com.example.notiveserver.infrastructure.security.SecurityCurrentUserProvider
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.util.*

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val archiveRepository: ArchiveRepository,
    private val userRepository: UserRepository,
    private val currentUser: SecurityCurrentUserProvider
) {
    @PreAuthorize("isAuthenticated()")
    fun listAllGroupsByUser(): List<GroupSummaryDto> {
        val userId = currentUser.id()
        return groupRepository.findAllByUserIdOrderByName(userId)
            .map { GroupSummaryDto(id = it.id!!, name = it.name) }
    }

    @PreAuthorize("isAuthenticated()")
    fun getGroup(groupId: UUID): GroupSummaryDto {
        return groupRepository.findById(groupId)
            .map { GroupSummaryDto(id = it.id!!, name = it.name) }
            .orElseThrow { ArchiveException(ArchiveErrorCode.GROUP_NOT_FOUND) }
    }

    @PreAuthorize("isAuthenticated()")
    fun listGroupsByUser(pageOffset: Int, pageSize: Int): Page<GroupSummaryDto> {
        val userId = currentUser.id()
        val pageable = PageRequest.of(pageOffset, pageSize)
        val pages = groupRepository.findByUserIdOrderByName(userId, pageable)
        return pages.map {
            GroupSummaryDto(it.id!!, it.name)
        }
    }

    @PreAuthorize("hasRole('ADMIN') or @accessManager.isGroupOwner(#group.id)")
    fun getDetailedGroup(group: GroupSummaryDto): GroupDetailDto {
        val top3Thumbnails =
            archiveRepository.findTop3ByGroupIdOrderByCreatedAtDesc(group.id)
        val totalElements = archiveRepository.countByGroupId(group.id)
        return GroupDetailDto(
            id = group.id,
            name = group.name,
            thumbnails = top3Thumbnails.map { it.thumbnailPath },
            totalElements = totalElements,
        )
    }


    @PreAuthorize("isAuthenticated()")
    fun createGroup(name: String): Group {
        val userId = currentUser.id()
        val exists = groupRepository.existsByUserIdAndName(userId, name)
        if (exists) {
            throw ArchiveException(ArchiveErrorCode.GROUP_ALREADY_EXISTS)
        }
        val user = userRepository.getReferenceById(userId)
        return groupRepository.save(Group(name = name, user = user))
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and @accessManager.isGroupOwner(#groupId)")
    fun updateGroup(groupId: UUID, name: String): Group {
        val userId = currentUser.id()
        val exists = groupRepository.existsByUserIdAndName(userId, name)
        if (exists) {
            throw ArchiveException(ArchiveErrorCode.GROUP_ALREADY_EXISTS)
        }
        val group = groupRepository.getReferenceById(groupId)
        group.name = name
        return groupRepository.save(group)
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and @accessManager.isGroupOwner(#groupId)")
    fun deleteGroupWithArchives(groupId: UUID) {
        archiveRepository.deleteAllByGroupId(groupId)
        groupRepository.deleteById(groupId)
    }
}