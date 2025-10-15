package com.example.notiveserver.common.validation.validator

import com.example.notiveserver.common.validation.annotation.ValidImageFile
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.web.multipart.MultipartFile

class ImageFileValidator : ConstraintValidator<ValidImageFile, MultipartFile?> {

    private var maxSize: Long = 0
    private lateinit var allowedExt: Set<String>

    override fun initialize(constraint: ValidImageFile) {
        maxSize = constraint.maxSize
        allowedExt = constraint.ext.map { it.lowercase() }.toSet()
    }

    override fun isValid(file: MultipartFile?, context: ConstraintValidatorContext): Boolean {
        if (file == null || file.isEmpty) return true

        if (file.size > maxSize) return false

        val ext = file.originalFilename?.substringAfterLast('.', "").orEmpty().lowercase()
        return ext in allowedExt
    }
}
