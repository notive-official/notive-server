package com.example.notiveserver.application.user

import com.example.notiveserver.application.user.dto.UserDto
import com.example.notiveserver.common.enums.ImageCategory
import com.example.notiveserver.common.exception.UserException
import com.example.notiveserver.common.exception.code.UserErrorCode
import com.example.notiveserver.domain.repository.UserRepository
import com.example.notiveserver.infrastructure.s3.S3StorageClient
import com.example.notiveserver.infrastructure.security.SecurityCurrentUserProvider
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class UserService(
    private val userRepository: UserRepository,
    private val s3StorageClient: S3StorageClient,
    private val currentUser: SecurityCurrentUserProvider
) {

    @PreAuthorize("isAuthenticated()")
    fun findCurrentUser(): UserDto {
        val userId = currentUser.id()
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)
        return UserDto(
            id = user.id!!,
            name = user.name,
            nickname = user.nickname,
            email = user.email,
            profileImagePath = user.profileImage
        )
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    fun uploadUserProfileImage(file: MultipartFile): String {
        val profileImagePath = s3StorageClient.saveImage(file, ImageCategory.PROFILE)
        val userId = currentUser.id()
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)
        user.profileImage = profileImagePath
        return profileImagePath
    }

    @PreAuthorize("isAuthenticated()")
    fun deleteUserProfileImage(): String {
        val userId = currentUser.id()
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)
        val profileImagePath =
            user.profileImage ?: throw UserException(UserErrorCode.USER_PROFILE_IMAGE_NOT_FOUND)
        s3StorageClient.deleteImage(profileImagePath)
        return profileImagePath
    }
}