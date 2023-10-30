package io.bluetape4k.workshop.es.domain.dto

import io.bluetape4k.workshop.es.domain.model.Book
import io.bluetape4k.workshop.es.metadata.PublicationYear
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive


data class CreateBookRequest(
    @NotBlank
    val title: String,

    @Positive
    @PublicationYear
    val publicationYear: Int,

    @NotBlank
    val authorName: String,

    @NotBlank
    val isbn: String,
)

fun CreateBookRequest.toBook(): Book {
    return Book(
        title = title,
        publicationYear = publicationYear,
        authorName = authorName,
        isbn = isbn,
    )
}
