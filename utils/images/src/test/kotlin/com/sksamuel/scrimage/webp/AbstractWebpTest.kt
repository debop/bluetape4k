package com.sksamuel.scrimage.webp

import com.sksamuel.scrimage.AbstractScrimageTest
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.metadata.ImageMetadata
import com.sksamuel.scrimage.metadata.Tag
import io.bluetape4k.logging.KLogging

abstract class AbstractWebpTest: AbstractScrimageTest() {

    companion object: KLogging()

    protected fun loadImage(filename: String): ImmutableImage {
        return ImmutableImage.loader().fromResource("/scrimage/webp/$filename")
    }

    fun ImageMetadata.prettyPrint(): String = buildString {
        directories.forEach { directory ->
            appendLine("name=${directory.name}")
            directory.tags.forEach { tag ->
                append("\t").appendLine("tag name=${tag.name}, value=${tag.value}")
            }
        }
    }

    fun ImageMetadata.printTags(): String = buildString {
        tagsAsSequence().forEach { tag ->
            appendLine("tag name=${tag.name}, value=${tag.value}")
        }
    }

    fun ImageMetadata.tagsAsSequence(): Sequence<Tag> {
        return directories.asSequence()
            .flatMap { dir -> dir.tags.asSequence() }
    }
}
