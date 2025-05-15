package com.example.notiveserver.controller

import com.example.notiveserver.common.enums.ImageCategory
import com.example.notiveserver.dto.user.HeaderResDto
import com.example.notiveserver.dto.user.ProfileResDto
import com.example.notiveserver.security.CustomUser
import com.example.notiveserver.service.S3Service
import com.example.notiveserver.service.UserService
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
    private val s3Service: S3Service,
) {

    @GetMapping("/header")
    @PreAuthorize("isAuthenticated()")
    fun header(@AuthenticationPrincipal auth: CustomUser): ResponseEntity<HeaderResDto> {
        val userId = auth.getUserId()
        val user = userService.findOneByUserId(userId)
        return ResponseEntity.ok(
            HeaderResDto(
                userId,
                user.nickname,
                "https://d210c6t8c7wtwt.cloudfront.net/" + user.profileImage
            )
        )
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    fun profile(@AuthenticationPrincipal auth: CustomUser): ResponseEntity<ProfileResDto> {
        val userId = auth.getUserId()
        val user = userService.findOneByUserId(userId)
        return ResponseEntity.ok(
            ProfileResDto(
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
    fun coverImageUpload(@RequestParam("file") file: MultipartFile): String {
        val amazonBucket: String = s3Service.saveFile(file, ImageCategory.PROFILE)
        return amazonBucket
    }

}