package com.example.notiveserver.domain.user.repository

import com.example.notiveserver.domain.user.model.Authority
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorityRepository : JpaRepository<Authority, Long> {
    fun findByName(name: String): Authority?
}
