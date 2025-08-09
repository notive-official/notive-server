package com.example.notiveserver.application.user

import com.example.notiveserver.application.user.dto.ProfileImageDto
import com.example.notiveserver.application.user.dto.UserSummaryDto
import com.example.notiveserver.common.exception.UserException
import com.example.notiveserver.common.exception.code.UserErrorCode
import com.example.notiveserver.domain.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun findOneByUserId(userId: UUID): UserSummaryDto {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)
        return UserSummaryDto(
            id = user.id!!,
            name = user.name,
            nickname = user.nickname,
            email = user.email,
            profileImage = ProfileImageDto.of(user.profileImage)
        )
    }
}