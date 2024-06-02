package io.bluetape4k.openai.api.models.file

import io.bluetape4k.codec.encodeBase64String
import io.bluetape4k.openai.api.annotations.OpenAIDsl
import io.bluetape4k.support.requireNotBlank
import io.bluetape4k.support.requireNotNull
import io.bluetape4k.support.toUtf8String
import java.io.Serializable
import java.nio.file.Files
import java.nio.file.Paths

data class FileSource(
    val name: String,
    val source: String,
): Serializable {

    companion object {
        val EMPTY = FileSource("", "")
    }

    fun toJson(): String {
        return if (source.isNotBlank()) {
            Files.readAllBytes(Paths.get(source)).toUtf8String().encodeBase64String()
        } else {
            ""
        }
    }
}

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
