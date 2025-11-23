package com.example.notiveserver.user

import com.example.notiveserver.api.controller.UserController
import com.example.notiveserver.application.user.UserService
import com.example.notiveserver.application.user.dto.UserDto
import com.example.notiveserver.domain.user.model.Authority
import com.example.notiveserver.domain.user.model.User
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.multipart
import java.util.*

@WebMvcTest(controllers = [UserController::class])
@Import(UserControllerTestConfig::class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val userService: UserService,
) {

    private val user = User(
        id = UUID.randomUUID(),
        name = "notive",
        nickname = "thisisnotive",
        email = "notive@gmail.com",
        profileImage = "/profileImage.png",
        socialId = "googleId",
        authorities = mutableSetOf(Authority(id = 1L, name = "ROLE_USER"))
    )

    @Test
    fun `유저 헤더 정보 조회 API가 200과 body를 반환한다`() {
        // given
        given(userService.findCurrentUser()).willReturn(
            UserDto(
                id = user.id!!,
                name = user.name,
                nickname = user.nickname,
                email = user.email,
                profileImagePath = user.profileImage
            )
        )

        // when & then
        mockMvc.get("/api/user/header")
            .andExpect {
                status { isOk() }
                jsonPath("$.nickname") { value(user.nickname) }
                jsonPath("$.profileImagePath") { value(user.profileImage) }
            }
    }

    @Test
    fun `유저 프로필 정보 조회 API가 200과 body를 반환한다`() {
        // given
        given(userService.findCurrentUser()).willReturn(
            UserDto(
                id = user.id!!,
                name = user.name,
                nickname = user.nickname,
                email = user.email,
                profileImagePath = user.profileImage
            )
        )

        // when & then
        mockMvc.get("/api/user/profile")
            .andExpect {
                status { isOk() }
                jsonPath("$.name") { value(user.name) }
                jsonPath("$.email") { value(user.email) }
                jsonPath("$.nickname") { value(user.nickname) }
                jsonPath("$.profileImagePath") { value(user.profileImage) }
            }
    }

    @Test
    fun `유저 프로필 이미지 변경 API가 200과 body를 반환한다`() {
        // given
        val mockFile = MockMultipartFile(
            "file",
            "profile.png",
            "image/png",
            "fake image bytes".toByteArray()
        )
        given(userService.uploadUserProfileImage(mockFile)).willReturn(
            user.profileImage
        )

        // when & then
        mockMvc.multipart("/api/user/profile/image") {
            file(mockFile)
            with { request ->            // 👈 request를 리턴해야 함
                request.method = "PUT"
                request
            }
        }.andExpect {
            status { isOk() }
            jsonPath("$.name") { value(user.name) }
            jsonPath("$.email") { value(user.email) }
            jsonPath("$.nickname") { value(user.nickname) }
            jsonPath("$.profileImagePath") { value(user.profileImage) }
        }
    }
}
