package com.example.notiveserver.application.archive

import com.example.notiveserver.common.exception.ArchiveException
import com.example.notiveserver.common.exception.code.ArchiveErrorCode
import com.example.notiveserver.common.util.SlugUtil
import com.example.notiveserver.domain.tag.model.Tag
import com.example.notiveserver.domain.archive.repository.ArchiveRepository
import com.example.notiveserver.domain.tag.repository.TagRepository
import com.example.notiveserver.infrastructure.security.SecurityCurrentUserProvider
import jakarta.transaction.Transactional
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.util.*

@Service
class TagService(
    private val tagRepository: TagRepository,
    private val archiveRepository: ArchiveRepository,
    private val currentUser: SecurityCurrentUserProvider
) {
    @PreAuthorize("isAuthenticated()")
    fun listTagsOwnedByUser(): List<String> {
        val userId = currentUser.id()
        return tagRepository.findDistinctByArchivesWriterIdOrderBySlugAsc(userId).map { it.slug }
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and @accessManager.isArchiveOwner(#archiveId)")
    fun updateTagsByArchive(rawTags: List<String>, archiveId: UUID): List<String> {
        val archive = archiveRepository.findById(archiveId)
            .orElseThrow { ArchiveException(ArchiveErrorCode.ARCHIVE_NOT_FOUND) }
        val newTagIds = getOrSave(rawTags).mapNotNull { it.id }.toMutableSet()
        val oldTagIds =
            tagRepository.findAllByArchivesIdOrderBySlugAsc(archiveId).mapNotNull { it.id }
                .toMutableSet()
        val toAdd = newTagIds - oldTagIds
        val toRemove = oldTagIds - newTagIds
        toAdd.forEach { id ->
            val ref = tagRepository.getReferenceById(id)
            archive.tags.add(ref)
            ref.archives.add(archive)
        }
        toRemove.forEach { id ->
            val ref = tagRepository.getReferenceById(id)
            archive.tags.remove(ref)
            ref.archives.remove(archive)
        }
        return archive.tags.map { it.slug }
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and @accessManager.isArchiveOwner(#archiveId)")
    fun saveTagsByArchive(rawTags: List<String>, archiveId: UUID): List<String> {
        val tags = getOrSave(rawTags)
        val archive = archiveRepository.getReferenceById(archiveId)
        archive.tags = tags.toMutableSet()
        return tags.map { it.slug }
    }

    fun listTagsByArchive(archiveId: UUID): List<String> {
        return tagRepository.findAllByArchivesIdOrderBySlugAsc(archiveId).map { it.slug }
    }

    private fun getOrSave(rawTags: List<String>): List<Tag> {
        return rawTags
            .mapNotNull { it.trim().takeIf(String::isNotBlank) }
            .map { tag ->
                val slug = SlugUtil.slugify(tag)
                tagRepository.findBySlug(slug) ?: tagRepository.save(Tag(slug = slug))
            }
    }
}