package com.example.notiveserver.domain.repository

import com.example.notiveserver.domain.model.user.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    @EntityGraph(attributePaths = ["authorities"])
    fun findWithAuthoritiesBySocialId(socialId: String): User?
}