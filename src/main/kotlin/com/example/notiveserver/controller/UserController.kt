package com.example.notiveserver.controller

import com.example.notiveserver.dto.user.HeaderResDto
import com.example.notiveserver.dto.user.ProfileResDto
import com.example.notiveserver.security.CustomUser
import com.example.notiveserver.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
) {

    @GetMapping("/header")
    @PreAuthorize("isAuthenticated()")
    fun header(@AuthenticationPrincipal auth: CustomUser): ResponseEntity<HeaderResDto> {
        val userId = auth.getUserId()
        val user = userService.findOneByUserId(userId)
        return ResponseEntity.ok(HeaderResDto(userId, user.nickname, user.profileImage))
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
}