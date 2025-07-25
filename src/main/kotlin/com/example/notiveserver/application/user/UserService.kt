package com.example.notiveserver.application.user

import com.example.notiveserver.common.exception.UserException
import com.example.notiveserver.common.exception.code.UserErrorCode
import com.example.notiveserver.domain.model.user.User
import com.example.notiveserver.domain.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun findOneByUserId(userId: UUID): User =
        userRepository.findByIdOrNull(userId)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)
}