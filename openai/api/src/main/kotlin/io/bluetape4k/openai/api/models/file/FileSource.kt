package io.bluetape4k.openai.api.models.file

import io.bluetape4k.core.requireNotBlank
import io.bluetape4k.core.requireNotNull
import io.bluetape4k.openai.api.annotations.OpenAIDsl
import java.io.Serializable

data class FileSource(
    val name: String,
    val source: String,
) : Serializable

fun fileSource(initializer: FileSourceBuilder.() -> Unit): FileSource =
    FileSourceBuilder().apply(initializer).build()

@OpenAIDsl
class FileSourceBuilder {

    var name: String? = null
    var source: String? = null

    fun build(): FileSource = FileSource(
        name = this.name.requireNotBlank("name"),
        source = this.source.requireNotNull("path")
    )
}
