package io.bluetape4k.openai.api.models.file

import io.bluetape4k.openai.api.models.Status
import java.io.Serializable

data class File(
    val id: FileId,
    val bytes: Int,
    val createdAt: Long,
    val filename: String,
    val purpose: Purpose,
    val status: Status? = null,
    val format: String? = null,
): Serializable
