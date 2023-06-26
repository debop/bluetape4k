package io.bluetape4k.openai.api.models.file

import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.core.requireNotNull
import io.bluetape4k.openai.api.OpenAIDsl
import java.nio.file.Path

data class FileSource(
    val name: String,
    val source: Path,
)

fun fileSource(initializer: FileSourceBuilder.() -> Unit): FileSource =
    FileSourceBuilder().apply(initializer).build()

@OpenAIDsl
class FileSourceBuilder {

    var name: String? = null
    var source: Path? = null

    fun build(): FileSource = FileSource(
        name = this.name.requireNotBlank("name"),
        source = this.source.requireNotNull("path")
    )
}
