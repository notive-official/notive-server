package com.example.notiveserver.domain.repository

import com.example.notiveserver.domain.model.user.Authority
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorityRepository : JpaRepository<Authority, Long> {
    fun findByName(name: String): Authority?
}
