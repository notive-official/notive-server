package com.example.notiveserver.service

import com.example.notiveserver.domain.entity.User
import com.example.notiveserver.domain.repository.UserRepository
import com.example.notiveserver.exception.UserException
import com.example.notiveserver.exception.code.UserErrorCode
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    fun findOneByUserId(userId: Long): User =
        userRepository.findByIdOrNull(userId)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)
}