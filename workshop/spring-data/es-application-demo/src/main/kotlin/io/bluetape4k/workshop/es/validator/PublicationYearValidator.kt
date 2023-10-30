package io.bluetape4k.workshop.es.validator

import io.bluetape4k.workshop.es.metadata.PublicationYear
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.time.Year

class PublicationYearValidator: ConstraintValidator<PublicationYear, Int> {
    override fun isValid(value: Int?, context: ConstraintValidatorContext): Boolean {
        return value?.let { !Year.of(it).isAfter(Year.now()) } ?: false
    }
}
