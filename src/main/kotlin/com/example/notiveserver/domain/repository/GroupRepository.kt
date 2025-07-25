package com.example.notiveserver.domain.repository

import com.example.notiveserver.domain.model.archive.Group
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GroupRepository : JpaRepository<Group, UUID> {
    fun findAllByUserId(userId: UUID): List<Group>
    fun existsByUserIdAndName(userId: UUID, name: String): Boolean
}
