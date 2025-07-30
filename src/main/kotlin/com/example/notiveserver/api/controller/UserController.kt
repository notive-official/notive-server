package com.example.notiveserver.api.controller

import com.example.notiveserver.api.dto.user.HeaderRes
import com.example.notiveserver.api.dto.user.ProfileRes
import com.example.notiveserver.application.user.UserService
import com.example.notiveserver.common.enums.ImageCategory
import com.example.notiveserver.infrastructure.s3.S3StorageClient
import com.example.notiveserver.infrastructure.security.dto.CustomUser
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException


@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
    private val s3StorageClient: S3StorageClient,
    @Value("\${cloud.aws.cloudfront.domain}") private val cloudFrontDomain: String
) {

    @GetMapping("/header")
    @PreAuthorize("isAuthenticated()")
    fun header(@AuthenticationPrincipal auth: CustomUser): ResponseEntity<HeaderRes> {
        val userId = auth.getId()
        val user = userService.findOneByUserId(userId)
        return ResponseEntity.ok(
            HeaderRes(
                userId,
                user.nickname,
                "$cloudFrontDomain${user.profileImage}"
            )
        )
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    fun profile(@AuthenticationPrincipal auth: CustomUser): ResponseEntity<ProfileRes> {
        val userId = auth.getId()
        val user = userService.findOneByUserId(userId)
        return ResponseEntity.ok(
            ProfileRes(
                user.name,
                user.nickname,
                user.email,
                user.profileImage
            )
        )
    }

    @PutMapping("/profile/image")
    @PreAuthorize("isAuthenticated()")
    @Throws(IOException::class)
    fun coverImageUpload(
        @AuthenticationPrincipal auth: CustomUser,
        @RequestParam("file") file: MultipartFile
    ): String {
        val filePath: String = s3StorageClient.saveImage(file, ImageCategory.PROFILE)
        return "$cloudFrontDomain$filePath"
    }

}