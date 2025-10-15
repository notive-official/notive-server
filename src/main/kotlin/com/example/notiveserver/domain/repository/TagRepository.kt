package com.example.notiveserver.domain.repository

import com.example.notiveserver.domain.model.archive.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TagRepository : JpaRepository<Tag, UUID> {
    fun findBySlug(slug: String): Tag?

    fun findDistinctByArchivesWriterIdOrderBySlugAsc(writerId: UUID): List<Tag>

    fun findAllByArchivesIdOrderBySlugAsc(archiveId: UUID): List<Tag>
}
