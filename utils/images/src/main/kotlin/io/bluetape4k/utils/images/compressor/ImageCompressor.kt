package io.bluetape4k.utils.images.compressor

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardOpenOption

interface ImageCompressor {

    fun compress(input: InputStream): ByteArray

    fun compress(srcFile: File, destFile: File) {
        FileInputStream(srcFile).buffered().use { input ->
            val compressed = compress(input)
            Files.write(destFile.toPath(), compressed, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
        }
    }
}
