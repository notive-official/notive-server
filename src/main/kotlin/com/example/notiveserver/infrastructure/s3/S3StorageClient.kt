package com.example.notiveserver.infrastructure.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.example.notiveserver.common.enums.ImageCategory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*


@Service
class S3StorageClient(
    @Value("\${cloud.aws.s3.bucket}") private val bucket: String,
    private val amazonS3: AmazonS3
) {

    @Throws(IOException::class)
    fun saveImage(multipartFile: MultipartFile, imageCategory: ImageCategory): String {
        val originalFilename = multipartFile.originalFilename

        val metadata = ObjectMetadata()
        metadata.contentLength = multipartFile.size
        metadata.contentType = multipartFile.contentType

        val directoryPath = imageCategory.getDirectoryPath()
        val fileName = "${UUID.randomUUID()}-$originalFilename"
        val filePath = "$directoryPath/$fileName"

        amazonS3.putObject(bucket, filePath, multipartFile.inputStream, metadata)
        return filePath
    }
}