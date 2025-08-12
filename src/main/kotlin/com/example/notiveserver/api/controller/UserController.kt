package com.example.notiveserver.api.controller

import com.example.notiveserver.api.dto.user.HeaderRes
import com.example.notiveserver.api.dto.user.ProfileRes
import com.example.notiveserver.application.user.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException


@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
) {

    @GetMapping("/header")
    fun header(): ResponseEntity<HeaderRes> {
        val user = userService.findCurrentUser()
        return ResponseEntity.ok(
            HeaderRes(
                user.id,
                user.nickname,
                user.profileImage.filePath
            )
        )
    }

    @GetMapping("/profile")
    fun profile(): ResponseEntity<ProfileRes> {
        val user = userService.findCurrentUser()
        return ResponseEntity.ok(
            ProfileRes(
                user.name,
                user.nickname,
                user.email,
                user.profileImage.filePath
            )
        )
    }

    @PutMapping("/profile/image")
    @Throws(IOException::class)
    fun profileImageUpload(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<ProfileRes> {
        val user = userService.updateUserProfileImage(file)
        return ResponseEntity.ok(
            ProfileRes(
                user.name,
                user.nickname,
                user.email,
                user.profileImage.filePath
            )
        )
    }
}