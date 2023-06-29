package io.bluetape4k.openai.api.models.file

import io.bluetape4k.core.requireNotNull
import io.bluetape4k.openai.api.annotations.OpenAIDsl
import io.bluetape4k.openai.api.models.ModelBuilder
import java.io.Serializable

/**
 * Request to upload a file.
 *
 * [documentation](https://beta.openai.com/docs/api-reference/files/upload)
 *
 * @property file The [JSON Lines](https://jsonlines.readthedocs.io/en/latest/) file to be uploaded.
 * If the [purpose] is set to "fine-tune", each line is a JSON record with "prompt" and "completion" fields
 * representing your [training examples](https://beta.openai.com/docs/guides/fine-tuning/prepare-training-data).
 *
 * @property purpose The intended purpose of the uploaded documents.
 * Use "fine-tune" for [Fine-tuning](https://beta.openai.com/docs/api-reference/fine-tunes).
 * This allows us to validate the format of the uploaded file.
 */
data class FileUpload(
    val file: FileSource,
    val purpose: Purpose,
): Serializable

inline fun fileUpload(initializer: FileUploadBuilder.() -> Unit): FileUpload =
    FileUploadBuilder().apply(initializer).build()

@OpenAIDsl
class FileUploadBuilder: ModelBuilder<FileUpload> {

    var file: FileSource? = null
    var purpose: Purpose? = null

    override fun build(): FileUpload {
        return FileUpload(
            file = this.file.requireNotNull("file"),
            purpose = this.purpose.requireNotNull("purpose")
        )
    }
}
