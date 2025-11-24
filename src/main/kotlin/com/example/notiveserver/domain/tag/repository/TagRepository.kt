package com.example.notiveserver.domain.tag.repository

import com.example.notiveserver.domain.tag.model.Tag
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TagRepository : JpaRepository<Tag, UUID> {
    fun findBySlug(slug: String): Tag?

    fun findDistinctByArchivesWriterIdOrderBySlugAsc(writerId: UUID): List<Tag>

    @Query(
        """
    SELECT DISTINCT t 
    FROM Tag t
    JOIN t.archives a
    WHERE a.id IN :archiveIds
    ORDER BY t.slug ASC

    """
    )
    fun findDistinctTagsByArchiveIds(archiveIds: List<UUID>): List<Tag>

    fun findAllByArchivesIdOrderBySlugAsc(archiveId: UUID): List<Tag>
}
