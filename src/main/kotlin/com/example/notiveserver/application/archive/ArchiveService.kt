package com.example.notiveserver.application.archive

import com.example.notiveserver.application.archive.dto.ArchiveDetailDto
import com.example.notiveserver.application.archive.dto.BlockInfoDto
import com.example.notiveserver.application.archive.dto.PayloadDto
import com.example.notiveserver.common.enums.ArchiveType
import com.example.notiveserver.common.enums.ImageCategory
import com.example.notiveserver.domain.archive.model.Archive
import com.example.notiveserver.domain.archive.repository.ArchiveBlockRepository
import com.example.notiveserver.domain.archive.repository.ArchiveRepository
import com.example.notiveserver.domain.group.repository.GroupRepository
import com.example.notiveserver.domain.user.repository.UserRepository
import com.example.notiveserver.infrastructure.s3.S3StorageClient
import com.example.notiveserver.infrastructure.security.SecurityCurrentUserProvider
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class ArchiveService(
    private val archiveRepository: ArchiveRepository,
    private val archiveBlockRepository: ArchiveBlockRepository,
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val s3StorageClient: S3StorageClient,
    private val currentUser: SecurityCurrentUserProvider
) {

    @Transactional
    @PreAuthorize("isAuthenticated() and @accessManager.isGroupOwner(#groupId)")
    fun saveArchive(
        thumbnailImage: MultipartFile?,
        title: String,
        isPublic: Boolean,
        type: ArchiveType,
        isDuplicable: Boolean,
        summary: String,
        groupId: UUID,
    ): Archive {
        val userId = currentUser.id()
        val thumbnailPath = thumbnailImage?.let { file ->
            s3StorageClient.saveImage(file, ImageCategory.ARCHIVE_THUMBNAIL)
        }
        return archiveRepository.save(
            Archive.create(
                thumbnailPath = thumbnailPath,
                title = title,
                isPublic = isPublic,
                type = type,
                isDuplicable = isDuplicable,
                summary = summary,
                group = groupRepository.getReferenceById(groupId),
                writer = userRepository.getReferenceById(userId)
            )
        )
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and @accessManager.isGroupOwner(#groupId)")
    fun saveArchive(
        thumbnailPath: String?,
        title: String,
        isPublic: Boolean,
        type: ArchiveType,
        isDuplicable: Boolean,
        summary: String,
        groupId: UUID,
    ): Archive {
        val userId = currentUser.id()
        return archiveRepository.save(
            Archive.create(
                thumbnailPath = thumbnailPath,
                title = title,
                isPublic = isPublic,
                type = type,
                isDuplicable = isDuplicable,
                summary = summary,
                group = groupRepository.getReferenceById(groupId),
                writer = userRepository.getReferenceById(userId)
            )
        )
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and @accessManager.isArchiveOwner(#archiveId)")
    fun updateArchive(
        archiveId: UUID,
        thumbnailImage: MultipartFile?,
        title: String?,
        isPublic: Boolean?,
        type: ArchiveType?,
        isDuplicable: Boolean?,
        summary: String?,
        groupId: UUID?,
    ): Archive {
        val archive = archiveRepository.getReferenceById(archiveId)
        thumbnailImage?.let { file ->
            archive.thumbnailPath?.let { s3StorageClient.deleteImage(it) }
            archive.thumbnailPath = s3StorageClient.saveImage(file, ImageCategory.ARCHIVE_THUMBNAIL)
        }
        title?.let { archive.title = it }
        isPublic?.let { archive.isPublic = it }
        type?.let { archive.type = it }
        isDuplicable?.let { archive.isDuplicable = it }
        summary?.let { archive.summary = it }
        groupId?.let { archive.group = groupRepository.getReferenceById(it) }
        return archive
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and @accessManager.isArchiveOwner(#archiveId)")
    fun updateArchive(
        archiveId: UUID,
        thumbnailPath: String?,
        title: String?,
        isPublic: Boolean?,
        type: ArchiveType?,
        isDuplicable: Boolean?,
        summary: String?,
        groupId: UUID?,
    ): Archive {
        val archive = archiveRepository.getReferenceById(archiveId)
        thumbnailPath?.let { archive.thumbnailPath = it }
        title?.let { archive.title = it }
        isPublic?.let { archive.isPublic = it }
        type?.let { archive.type = it }
        isDuplicable?.let { archive.isDuplicable = it }
        summary?.let { archive.summary = it }
        groupId?.let { archive.group = groupRepository.getReferenceById(it) }
        return archive
    }

    fun generateArchiveSummary(blockInfos: List<BlockInfoDto>): String {
        return blockInfos
            .mapNotNull { block ->
                when (val payload = block.payload) {
                    is PayloadDto.Text -> payload.text
                    else -> null
                }
            }
            .joinToString(" ").take(95)
    }

    @Transactional
    @PreAuthorize("@accessManager.canReadArchive(#archiveId)")
    fun getArchive(archiveId: UUID): ArchiveDetailDto {
        val archive = archiveRepository.findByIdOrNull(archiveId)!!
        val blocks = archiveBlockRepository.findAllByArchiveId(archiveId)
        return ArchiveDetailDto.of(archive, archive.writer, archive.group, blocks)
    }

    @Transactional
    @PreAuthorize("@accessManager.isArchiveOwner(#archiveId)")
    fun deleteArchive(archiveId: UUID) {
        archiveBlockRepository.deleteByArchiveId(archiveId)
        archiveRepository.deleteById(archiveId)
    }

    @Transactional
    @PreAuthorize("@accessManager.isArchiveOwner(#archiveId)")
    fun deleteThumbnail(archiveId: UUID) {
        val archive = archiveRepository.getReferenceById(archiveId)
        archive.thumbnailPath?.let { s3StorageClient.deleteImage(it) }
        archive.thumbnailPath = null
    }

    fun isArchiveOwner(archiveId: UUID): Boolean {
        try {
            val archive = archiveRepository.findByIdOrNull(archiveId)
            return archive!!.writer.id == currentUser.id()
        } catch (e: Exception) {
            return false
        }
    }
}