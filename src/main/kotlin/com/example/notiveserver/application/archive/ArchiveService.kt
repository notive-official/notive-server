package com.example.notiveserver.application.archive

import com.example.notiveserver.api.dto.archive.BlockRaw
import com.example.notiveserver.application.archive.dto.BlockInfoDto
import com.example.notiveserver.common.enums.ImageCategory
import com.example.notiveserver.common.exception.ArchiveException
import com.example.notiveserver.common.exception.code.ArchiveErrorCode
import com.example.notiveserver.domain.model.archive.Archive
import com.example.notiveserver.domain.model.archive.ArchiveBlock
import com.example.notiveserver.domain.repository.*
import com.example.notiveserver.infrastructure.s3.S3StorageClient
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class ArchiveService(
    private val archiveRepository: ArchiveRepository,
    private val archiveBlockRepository: ArchiveBlockRepository,
    private val groupRepository: GroupRepository,
    private val tagRepository: TagRepository,
    private val s3StorageClient: S3StorageClient,
    private val userRepository: UserRepository
) {

    @Transactional
    fun saveArchive(
        userId: UUID,
        thumbnailImage: MultipartFile?,
        title: String,
        isPublic: Boolean,
        groupId: UUID,
        tags: List<String>,
        contents: List<BlockInfoDto>
    ): Archive {
        val thumbnailPath: String? =
            thumbnailImage
                ?.takeIf { !it.isEmpty }
                ?.let { file ->
                    s3StorageClient.saveImage(file, ImageCategory.ARCHIVE_THUMBNAIL)
                }
        val user = userRepository.getReferenceById(userId)
        val group = groupRepository.getReferenceById(groupId)
        val archive =
            archiveRepository.save(
                Archive.create(
                    thumbnailPath,
                    title,
                    tags,
                    isPublic,
                    group,
                    user,
                    tagRepository
                )
            )
        saveContentBlocks(contents, archive)
        return archive
    }

    @Transactional
    fun saveContentBlocks(contents: List<BlockInfoDto>, archive: Archive): List<ArchiveBlock> {
        val contentBlocks = contents.map {
            ArchiveBlock.create(
                position = it.position,
                type = it.blockType,
                payload = it.payload,
                archive = archive
            )
        }
        return archiveBlockRepository.saveAll(contentBlocks)
    }

    fun convertToBlockInfoWithSavingImage(block: BlockRaw): BlockInfoDto {
        require(block.image != null) { ArchiveException(ArchiveErrorCode.IMAGE_REQUIRED) }
        val filePath = s3StorageClient.saveImage(block.image, ImageCategory.ARCHIVE_BLOCK)
        return BlockInfoDto(
            position = block.position,
            blockType = block.blockType,
            payload = filePath
        )
    }

    fun convertToBlockInfoWithoutSavingImage(block: BlockRaw): BlockInfoDto {
        require(block.content != null) { ArchiveException(ArchiveErrorCode.CONTENT_REQUIRED) }
        return BlockInfoDto(
            position = block.position,
            blockType = block.blockType,
            payload = block.content
        )
    }
}