package com.example.notiveserver.domain.repository

import com.example.notiveserver.domain.model.user.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    @EntityGraph(attributePaths = ["authorities"])
    fun findWithAuthoritiesBySocialId(socialId: String): User?
}