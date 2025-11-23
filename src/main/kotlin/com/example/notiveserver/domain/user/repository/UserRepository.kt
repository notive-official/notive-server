package com.example.notiveserver.domain.user.repository

import com.example.notiveserver.domain.user.model.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    @EntityGraph(attributePaths = ["authorities"])
    fun findWithAuthoritiesBySocialId(socialId: String): User?
}