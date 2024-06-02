package io.bluetape4k.workshop.es.domain.dto

import io.bluetape4k.workshop.es.domain.metadata.PublicationYear
import io.bluetape4k.workshop.es.domain.model.Book
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive


data class ModifyBookRequest(
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

fun ModifyBookRequest.toBook(): Book {
    return Book(
        title = title,
        publicationYear = publicationYear,
        authorName = authorName,
        isbn = isbn,
    )
}

fun Book.toModifyBookRequest(): ModifyBookRequest {
    return ModifyBookRequest(
        title = title,
        publicationYear = publicationYear,
        authorName = authorName,
        isbn = isbn,
    )
}
