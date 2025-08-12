package com.example.notiveserver.application.user

import com.example.notiveserver.application.user.dto.ProfileImageDto
import com.example.notiveserver.application.user.dto.UserDto
import com.example.notiveserver.common.enums.ImageCategory
import com.example.notiveserver.common.exception.UserException
import com.example.notiveserver.common.exception.code.UserErrorCode
import com.example.notiveserver.domain.model.user.User
import com.example.notiveserver.domain.repository.UserRepository
import com.example.notiveserver.infrastructure.s3.S3StorageClient
import com.example.notiveserver.infrastructure.security.SecurityUtils
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class UserService(
    private val userRepository: UserRepository,
    private val s3StorageClient: S3StorageClient
) {

    @PreAuthorize("isAuthenticated()")
    fun findCurrentUser(): UserDto {
        val userId = SecurityUtils.currentUserId
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)
        return UserDto(
            id = user.id!!,
            name = user.name,
            nickname = user.nickname,
            email = user.email,
            profileImage = ProfileImageDto.of(user.profileImage)
        )
    }

    @PreAuthorize("isAuthenticated()")
    fun updateUserProfileImage(file: MultipartFile): UserDto {
        val profileImagePath = s3StorageClient.saveImage(file, ImageCategory.PROFILE)
        val userId = SecurityUtils.currentUserId
        val user = userRepository.findByIdOrNull(userId)?.let { user: User ->
            user.profileImage = profileImagePath
            userRepository.save(user)
        } ?: throw UserException(UserErrorCode.USER_NOT_FOUND)
        return UserDto(
            id = user.id!!,
            name = user.name,
            nickname = user.nickname,
            email = user.email,
            profileImage = ProfileImageDto.of(user.profileImage)
        )
    }
}