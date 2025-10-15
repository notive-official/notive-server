package com.example.notiveserver.domain.repository

import com.example.notiveserver.domain.model.archive.Bookmark
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface BookmarkRepository : JpaRepository<Bookmark, UUID> {
    fun findByArchiveIdAndUserId(archiveId: UUID, userId: UUID): Optional<Bookmark>
    fun findByArchiveId(archiveId: UUID): Optional<Bookmark>
}
