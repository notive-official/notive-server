package com.example.notiveserver.api.validator.image

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Constraint(validatedBy = [ImageFileValidator::class])
annotation class ValidImageFile(
    val maxSize: Long = 5_000_000,
    val ext: Array<String> = ["jpg", "jpeg", "png"],
    val message: String = "잘못된 이미지 파일입니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)