package io.bluetape4k.workshop.es.metadata

import io.bluetape4k.workshop.es.validator.PublicationYearValidator
import jakarta.validation.Constraint

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.PROPERTY)
@Constraint(validatedBy = [PublicationYearValidator::class])
annotation class PublicationYear {
}
