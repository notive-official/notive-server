package com.example.notiveserver.application.archive

import com.example.notiveserver.application.archive.dto.ArchiveDetailDto
import com.example.notiveserver.application.archive.dto.ArchiveSummaryDto
import com.example.notiveserver.application.archive.dto.BlockInfoDto
import com.example.notiveserver.application.archive.dto.PayloadDto
import com.example.notiveserver.common.enums.ArchiveType
import com.example.notiveserver.common.enums.ImageCategory
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
        type: ArchiveType,
        isReplicable: Boolean,
        summary: String,
        groupId: UUID,
        tags: List<String>,
    ): Archive {
        val userId = SecurityUtils.currentUserId
        val thumbnailPath = thumbnailImage?.let { file ->
            s3StorageClient.saveImage(file, ImageCategory.ARCHIVE_THUMBNAIL)
        }
        return archiveRepository.save(
            Archive.create(
                thumbnailPath = thumbnailPath,
                title = title,
                isPublic = isPublic,
                type = type,
                isReplicable = isReplicable,
                summary = summary,
                tags = tagService.getOrSave(tags),
                group = groupRepository.getReferenceById(groupId),
                writer = userRepository.getReferenceById(userId)
            )
        )
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
    @PreAuthorize("@ownershipSecurity.isArchiveOwner(#archiveId)")
    fun saveArchiveBlocks(blocks: List<BlockInfoDto>, archiveId: UUID): List<ArchiveBlock> {
        val archive = archiveRepository.getReferenceById(archiveId)
        val archiveBlocks = blocks.map { block ->
            when (block.payload) {
                is PayloadDto.File -> {
                    val filePath =
                        s3StorageClient.saveImage(block.payload.file, ImageCategory.ARCHIVE_BLOCK)
                    block.toArchiveBlock(filePath, archive)
                }

                is PayloadDto.Url -> block.toArchiveBlock(block.payload.url, archive)
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
            ArchiveSummaryDto.of(archive, writer)
        }
    }

    @Transactional
    fun listPublicArchives(pageOffset: Int, pageSize: Int): Page<ArchiveSummaryDto> {
        // TODO: 동적 쿼리 적용
        val pageable = PageRequest.of(pageOffset, pageSize)
        val pages = archiveRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable)
        return pages.map { archive ->
            val writer = archive.writer
            ArchiveSummaryDto.of(archive, writer)
        }
    }

    @Transactional
    @PreAuthorize("@ownershipSecurity.isArchiveOwner(#archiveId)")
    fun getArchive(archiveId: UUID): ArchiveDetailDto {
        val archive = archiveRepository.findByIdOrNull(archiveId)!!
        val blocks = archiveBlockRepository.findAllByArchiveId(archiveId)
        return ArchiveDetailDto.of(archive, archive.writer, blocks)
    }
}