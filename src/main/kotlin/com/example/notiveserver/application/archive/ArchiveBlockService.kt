package com.example.notiveserver.application.archive

import com.example.notiveserver.application.archive.dto.BlockInfoDto
import com.example.notiveserver.application.archive.dto.BlockPositionDto
import com.example.notiveserver.application.archive.dto.PayloadDto
import com.example.notiveserver.common.enums.BlockType
import com.example.notiveserver.common.enums.ImageCategory
import com.example.notiveserver.common.exception.ArchiveException
import com.example.notiveserver.common.exception.code.ArchiveErrorCode
import com.example.notiveserver.domain.archive.model.ArchiveBlock
import com.example.notiveserver.domain.archive.repository.ArchiveBlockRepository
import com.example.notiveserver.domain.archive.repository.ArchiveRepository
import com.example.notiveserver.infrastructure.s3.S3StorageClient
import jakarta.transaction.Transactional
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.util.*

@Service
class ArchiveBlockService(
    private val archiveRepository: ArchiveRepository,
    private val archiveBlockRepository: ArchiveBlockRepository,
    private val s3StorageClient: S3StorageClient,
) {

    @Transactional
    @PreAuthorize("isAuthenticated() and @accessManager.isArchiveOwner(#archiveId)")
    fun saveArchiveBlocks(archiveId: UUID, blocks: List<BlockInfoDto>): List<ArchiveBlock> {
        val archive = archiveRepository.getReferenceById(archiveId)
        val archiveBlocks = blocks.map { block ->
            when (block.payload) {
                is PayloadDto.File -> {
                    val filePath =
                        s3StorageClient.saveImage(block.payload.file, ImageCategory.ARCHIVE_BLOCK)
                    block.createArchiveBlock(filePath, archive)
                }

                is PayloadDto.Url -> block.createArchiveBlock(block.payload.url, archive)
                is PayloadDto.Text -> block.createArchiveBlock(block.payload.text, archive)
            }
        }
        return archiveBlockRepository.saveAll(archiveBlocks)
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and @accessManager.isBelongsToArchive(#archiveId, #blocks.![id])")
    fun updateArchiveBlocksPayload(
        archiveId: UUID,
        blocks: List<BlockInfoDto>,
    ): List<ArchiveBlock> {
        val archiveBlocks = blocks.map { dto ->
            val block = archiveBlockRepository.findById(dto.id!!).orElseThrow {
                ArchiveException(ArchiveErrorCode.ARCHIVE_BLOCK_NOT_FOUND)
            }
            when (dto.payload) {
                is PayloadDto.File -> {
                    block.payload =
                        s3StorageClient.saveImage(dto.payload.file, ImageCategory.ARCHIVE_BLOCK)
                }

                is PayloadDto.Url -> block.payload = dto.payload.url
                is PayloadDto.Text -> block.payload = dto.payload.text
            }
            block
        }
        return archiveBlocks
    }

    @PreAuthorize("isAuthenticated() and @accessManager.isBelongsToArchive(#archiveId, #blockIds)")
    fun deleteArchiveBlocks(archiveId: UUID, blockIds: List<Long>) {
        archiveBlockRepository.deleteAllById(blockIds)
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and @accessManager.isBelongsToArchive(#archiveId, #blocks.![id])")
    fun reorderArchiveBlocks(
        archiveId: UUID,
        blocks: List<BlockPositionDto>
    ): List<ArchiveBlock> {
        return blocks.map { dto ->
            archiveBlockRepository.getReferenceById(dto.id).apply {
                position = dto.position
            }
        }
    }

    @PreAuthorize("isAuthenticated() and @accessManager.isArchiveOwner(#archiveId)")
    fun deleteArchiveBlocks(archiveId: UUID) {
        val archiveBlocks = archiveBlockRepository.findAllByArchiveId(archiveId)
        archiveBlocks.forEach { archiveBlock ->
            if (archiveBlock.type == BlockType.IMAGE) {
                s3StorageClient.deleteImage(archiveBlock.payload)
            }
        }
        archiveBlockRepository.deleteAll(archiveBlocks)
    }
}