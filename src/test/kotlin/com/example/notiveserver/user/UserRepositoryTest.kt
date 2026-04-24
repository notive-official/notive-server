package com.example.notiveserver.user

import com.example.notiveserver.domain.user.model.Authority
import com.example.notiveserver.domain.user.model.User
import com.example.notiveserver.domain.user.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import kotlin.test.Test

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest @Autowired constructor(
    val userRepository: UserRepository,
) {
    private val user = User(
        name = "notive",
        nickname = "thisisnotive",
        email = "notive@gmail.com",
        profileImage = "/profileImage",
        socialId = "googleId",
        authorities = mutableSetOf(Authority(id = 1L, name = "ROLE_USER"))
    )

    @Test
    fun `socialId로 사용자를 권한 정보와 함께 찾을 수 있다`() {
        // given
        val saved = userRepository.save(user)

        // when
        val found = userRepository.findWithAuthoritiesBySocialId(user.socialId)

        // then
        assertNotNull(found)
        assertEquals(saved.id, found!!.id)
        assertNotNull(found.authorities)
    }
}