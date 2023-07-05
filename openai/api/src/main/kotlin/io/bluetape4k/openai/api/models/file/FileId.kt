package io.bluetape4k.openai.api.models.file

import java.io.Serializable

/**
 * File identifier
 *
 * @property id file identifier
 */
@JvmInline
value class FileId(val id: String): Serializable
