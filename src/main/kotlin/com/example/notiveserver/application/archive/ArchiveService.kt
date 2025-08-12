package com.example.notiveserver.application.archive

import com.example.notiveserver.api.dto.my.archive.BlockFormReq
import com.example.notiveserver.application.archive.dto.ArchiveSummaryDto
import com.example.notiveserver.application.archive.dto.ArchiveThumbnailDto
import com.example.notiveserver.application.archive.dto.BlockInfoDto
import com.example.notiveserver.application.archive.dto.PayloadDto
import com.example.notiveserver.application.user.dto.ProfileImageDto
import com.example.notiveserver.application.user.dto.UserSummaryDto
import com.example.notiveserver.common.enums.BlockType
import com.example.notiveserver.common.enums.ImageCategory
import com.example.notiveserver.common.exception.ArchiveException
import com.example.notiveserver.common.exception.code.ArchiveErrorCode
import com.example.notiveserver.domain.model.archive.Archive
import com.example.notiveserver.domain.model.archive.ArchiveBlock
import com.example.notiveserver.domain.repository.ArchiveBlockRepository
import com.example.notiveserver.domain.repository.ArchiveRepository
import com.example.notiveserver.domain.repository.GroupRepository
import com.example.notiveserver.domain.repository.UserRepository
import com.example.notiveserver.infrastructure.s3.S3StorageClient
import com.example.notiveserver.infrastructure.security.SecurityUtils
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class ArchiveService(
    private val archiveRepository: ArchiveRepository,
    private val archiveBlockRepository: ArchiveBlockRepository,
    private val groupRepository: GroupRepository,
    private val s3StorageClient: S3StorageClient,
    private val userRepository: UserRepository,
    private val tagService: TagService,
) {

    @Transactional
    @PreAuthorize("isAuthenticated()")
    fun saveArchive(
        thumbnailImage: MultipartFile?,
        title: String,
        isPublic: Boolean,
        groupId: UUID,
        tags: List<String>,
        blocks: List<BlockInfoDto>
    ): Archive {
        val userId = SecurityUtils.currentUserId
        val thumbnailPath = thumbnailImage?.let { file ->
            s3StorageClient.saveImage(file, ImageCategory.ARCHIVE_THUMBNAIL)
        }
        val archive = archiveRepository.save(
            Archive.create(
                thumbnailPath = thumbnailPath,
                title = title,
                isPublic = isPublic,
                tags = tagService.getOrSave(tags),
                group = groupRepository.getReferenceById(groupId),
                writer = userRepository.getReferenceById(userId)
            )
        )
        saveArchiveBlocks(blocks, archive.id!!)
        return archive
    }

    fun validateAndGetPayload(block: BlockFormReq): PayloadDto {
        return when (block.type) {
            BlockType.IMAGE ->
                PayloadDto.File(
                    block.image ?: throw ArchiveException(ArchiveErrorCode.IMAGE_REQUIRED)
                )

            else ->
                PayloadDto.Text(
                    block.content ?: throw ArchiveException(ArchiveErrorCode.CONTENT_REQUIRED)
                )
        }
    }

    @Transactional
    @PreAuthorize("@ownershipSecurity.isArchiveOwner(archiveId, principal.id)")
    fun saveArchiveBlocks(blocks: List<BlockInfoDto>, archiveId: UUID): List<ArchiveBlock> {
        val archive = archiveRepository.getReferenceById(archiveId)
        val archiveBlocks = blocks.map { block ->
            when (block.payload) {
                is PayloadDto.File -> {
                    val filePath =
                        s3StorageClient.saveImage(block.payload.file, ImageCategory.ARCHIVE_BLOCK)
                    block.toArchiveBlock(filePath, archive)
                }

                is PayloadDto.Text -> block.toArchiveBlock(block.payload.text, archive)
            }
        }
        return archiveBlockRepository.saveAll(archiveBlocks)
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    fun listArchivesByUser(pageOffset: Int, pageSize: Int): Page<ArchiveSummaryDto> {
        val userId = SecurityUtils.currentUserId
        val pageable = PageRequest.of(pageOffset, pageSize)
        val pages = archiveRepository.findByWriterIdOrderByCreatedAtDesc(userId, pageable)
        return pages.map { archive ->
            val writer = archive.writer
            ArchiveSummaryDto(
                id = requireNotNull(archive.id),
                title = archive.title,
                thumbnailPath = ArchiveThumbnailDto.of(archive.thumbnailPath),
                writer = UserSummaryDto(
                    id = requireNotNull(writer.id),
                    nickname = writer.name,
                    profileImage = ProfileImageDto.of(writer.profileImage)
                )
            )
        }
    }

    @Transactional
    fun listPublicArchives(pageOffset: Int, pageSize: Int): Page<ArchiveSummaryDto> {
        val pageable = PageRequest.of(pageOffset, pageSize)
        val pages = archiveRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable)
        return pages.map { archive ->
            val writer = archive.writer
            ArchiveSummaryDto(
                id = requireNotNull(archive.id),
                title = archive.title,
                thumbnailPath = ArchiveThumbnailDto.of(archive.thumbnailPath),
                writer = UserSummaryDto(
                    id = requireNotNull(writer.id),
                    nickname = writer.name,
                    profileImage = ProfileImageDto.of(writer.profileImage)
                )
            )
        }
    }
}