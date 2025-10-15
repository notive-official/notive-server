package com.example.notiveserver.common.validation.validator

import com.example.notiveserver.common.validation.annotation.AtLeastOneNotNull
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kotlin.reflect.full.memberProperties

class AtLeastOneNotNullValidator : ConstraintValidator<AtLeastOneNotNull, Any> {
    private lateinit var fieldNames: Array<String>

    override fun initialize(constraintAnnotation: AtLeastOneNotNull) {
        this.fieldNames = constraintAnnotation.fieldNames
    }

    override fun isValid(value: Any?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true

        val valid = fieldNames.any { fieldName ->
            val field = value::class.memberProperties.find { it.name == fieldName }
            field?.getter?.call(value) != null
        }

        if (!valid) {
            val fieldsText = fieldNames.joinToString(", ")
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(
                "$fieldsText 중 하나는 필수입니다."
            ).addConstraintViolation()
        }

        return valid
    }
}