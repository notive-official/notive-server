package com.example.notiveserver.application.archive

import com.example.notiveserver.common.exception.ArchiveException
import com.example.notiveserver.common.exception.code.ArchiveErrorCode
import com.example.notiveserver.domain.model.archive.Group
import com.example.notiveserver.domain.repository.GroupRepository
import com.example.notiveserver.domain.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class GroupService(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository
) {
    fun listUserGroups(userId: UUID): List<Group> = groupRepository.findAllByUserId(userId)

    fun createGroup(name: String, userId: UUID): Group {
        val exists = groupRepository.existsByUserIdAndName(userId, name)
        if (exists) {
            throw ArchiveException(ArchiveErrorCode.GROUP_ALREADY_EXISTS)
        }
        val userProxy = userRepository.getReferenceById(userId)
        return groupRepository.save(Group(name = name, user = userProxy))
    }
}