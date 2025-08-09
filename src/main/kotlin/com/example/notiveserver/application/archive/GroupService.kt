package com.example.notiveserver.application.archive

import com.example.notiveserver.application.archive.dto.ArchiveThumbnailDto
import com.example.notiveserver.application.archive.dto.GroupDetailDto
import com.example.notiveserver.application.archive.dto.GroupSummaryDto
import com.example.notiveserver.common.exception.ArchiveException
import com.example.notiveserver.common.exception.code.ArchiveErrorCode
import com.example.notiveserver.domain.model.archive.Group
import com.example.notiveserver.domain.repository.ArchiveRepository
import com.example.notiveserver.domain.repository.GroupRepository
import com.example.notiveserver.domain.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.util.*

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val archiveRepository: ArchiveRepository,
    private val userRepository: UserRepository
) {
    fun listAllGroupsByUser(userId: UUID): List<Group> =
        groupRepository.findAllByUserIdOrderByName(userId)

    fun listGroupsByUser(userId: UUID, pageOffset: Int, pageSize: Int): Page<Group> {
        val pageable = PageRequest.of(pageOffset, pageSize)
        return groupRepository.findByUserIdOrderByName(userId, pageable)
    }

    @PreAuthorize("hasRole('ADMIN') or @ownershipSecurity.isGroupOwner(#group.id, principal.id)")
    fun getGroupDetails(group: GroupSummaryDto): GroupDetailDto {
        val top3Thumbnails =
            archiveRepository.findTop3ByGroupIdOrderByCreatedAtDesc(group.id)
        val totalElements = archiveRepository.countByGroupId(group.id)
        return GroupDetailDto(
            id = group.id,
            name = group.name,
            thumbnails = top3Thumbnails.map { ArchiveThumbnailDto.of(it.thumbnailPath) },
            totalElements = totalElements,
        )
    }

    fun createGroup(name: String, userId: UUID): Group {
        val exists = groupRepository.existsByUserIdAndName(userId, name)
        if (exists) {
            throw ArchiveException(ArchiveErrorCode.GROUP_ALREADY_EXISTS)
        }
        val userProxy = userRepository.getReferenceById(userId)
        return groupRepository.save(Group(name = name, user = userProxy))
    }
}