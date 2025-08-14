package com.example.notiveserver.common.validation.annotation

import com.example.notiveserver.common.validation.validator.AtLeastOneNotNullValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [AtLeastOneNotNullValidator::class])
annotation class AtLeastOneNotNull(
    val fieldNames: Array<String>,
    val message: String = "다음 중 하나는 필수입니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
