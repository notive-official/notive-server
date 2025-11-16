package com.example.notiveserver.user

import com.example.notiveserver.application.user.UserService
import com.example.notiveserver.application.user.dto.UserDto
import com.example.notiveserver.common.enums.ImageCategory
import com.example.notiveserver.common.exception.UserException
import com.example.notiveserver.common.exception.code.UserErrorCode
import com.example.notiveserver.domain.model.user.Authority
import com.example.notiveserver.domain.model.user.User
import com.example.notiveserver.domain.repository.UserRepository
import com.example.notiveserver.infrastructure.s3.S3StorageClient
import com.example.notiveserver.infrastructure.security.SecurityCurrentUserProvider
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import org.springframework.mock.web.MockMultipartFile
import java.util.*
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class UserServiceTest {

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var s3StorageClient: S3StorageClient

    @MockK
    lateinit var currentUser: SecurityCurrentUserProvider

    @InjectMockKs
    lateinit var userService: UserService

    private val user = User(
        id = UUID.randomUUID(),
        name = "notive",
        nickname = "thisisnotive",
        email = "notive@gmail.com",
        profileImage = "/profileImage",
        socialId = "googleId",
        authorities = mutableSetOf(Authority(id = 1L, name = "ROLE_USER"))
    )

    @Test
    fun `로그인된 유저 조회 시 유저가 존재하면 UserDto를 반환한다`() {
        // given
        every { userRepository.findByIdOrNull(user.id!!) } returns user
        every { currentUser.id() } returns user.id!!

        // when
        val result = userService.findCurrentUser()

        // then
        assertEquals(
            UserDto(
                id = user.id!!,
                name = user.name,
                nickname = user.nickname,
                email = user.email,
                profileImagePath = user.profileImage
            ), result
        )
    }

    @Test
    fun `로그인된 유저 조회 시 유저가 없으면 예외를 반환한다`() {
        // given
        every { currentUser.id() } returns user.id!!
        every { userRepository.findByIdOrNull(user.id!!) } returns null

        // when
        val ex = assertThrows<UserException> {
            userService.findCurrentUser()
        }

        // then
        assertEquals(UserErrorCode.USER_NOT_FOUND.httpStatus, ex.httpStatus)
        assertEquals(UserErrorCode.USER_NOT_FOUND.message, ex.message)
    }

    @Test
    fun `유저 프로필 이미지 업로드 시 프로필 이미지 경로를 반환한다`() {
        // given
        val mockFile = MockMultipartFile(
            "file",
            "profile.png",
            "image/png",
            "fake image bytes".toByteArray()
        )
        every { currentUser.id() } returns user.id!!
        every { userRepository.findByIdOrNull(user.id!!) } returns user
        every {
            s3StorageClient.saveImage(
                mockFile,
                ImageCategory.PROFILE
            )
        } returns "/profile/changedProfile.png"

        // when
        val result = userService.uploadUserProfileImage(mockFile)

        // then
        assertEquals("/profile/changedProfile.png", result)
    }

    @Test
    fun `유저 프로필 이미지 삭제 시 삭제된 프로필 이미지 경로를 반환한다`() {
        // given
        every { currentUser.id() } returns user.id!!
        every { userRepository.findByIdOrNull(user.id!!) } returns user
        every { s3StorageClient.deleteImage(user.profileImage!!) } returns Unit

        // when
        val result = userService.deleteUserProfileImage()

        // then
        assertEquals(user.profileImage, result)
    }

    @Test
    fun `유저 프로필 이미지 삭제 시 유저의 프로필이 없으면 예외를 반환한다`() {
        // given
        user.profileImage = null
        every { currentUser.id() } returns user.id!!
        every { userRepository.findByIdOrNull(user.id!!) } returns user

        // when
        val ex = assertThrows<UserException> {
            userService.deleteUserProfileImage()
        }

        // then
        assertEquals(UserErrorCode.USER_PROFILE_IMAGE_NOT_FOUND.httpStatus, ex.httpStatus)
        assertEquals(UserErrorCode.USER_PROFILE_IMAGE_NOT_FOUND.message, ex.message)
    }
}