package io.wrtn.kommons.images

import com.sksamuel.scrimage.ImmutableImage
import io.wrtn.kommons.images.coroutines.CoImageWriter
import io.wrtn.kommons.images.coroutines.CoWriteContext
import io.wrtn.kommons.io.readAllBytesSuspending
import io.wrtn.kommons.io.writeSuspending
import java.awt.Graphics2D
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.nio.file.Path

fun immutableImageOf(bytes: ByteArray): ImmutableImage =
    ImmutableImage.loader().fromBytes(bytes)

fun immutableImageOf(inputStream: InputStream): ImmutableImage =
    ImmutableImage.loader().fromStream(inputStream.buffered())

fun immutableImageOf(file: File): ImmutableImage =
    ImmutableImage.loader().fromFile(file)

fun immutableImageOf(path: Path): ImmutableImage =
    ImmutableImage.loader().fromPath(path)

suspend fun immutableImageOfSuspending(file: File): ImmutableImage =
    immutableImageOfSuspending(file.toPath())

suspend fun immutableImageOfSuspending(path: Path): ImmutableImage =
    immutableImageOf(path.readAllBytesSuspending())


suspend fun loadImageSuspending(file: File): ImmutableImage {
    return loadImageSuspending(file.toPath())
}

suspend fun loadImageSuspending(path: Path): ImmutableImage {
    return immutableImageOf(path.readAllBytesSuspending())
}

suspend fun ImmutableImage.bytesSuspending(writer: CoImageWriter): ByteArray {
    return ByteArrayOutputStream().use { bos ->
        writer.writeSuspending(this, this.metadata, bos)
        bos.toByteArray()
    }
}

suspend fun ImmutableImage.writeSuspending(writer: CoImageWriter, destPath: Path): Long {
    val bytes = bytesSuspending(writer)
    return destPath.writeSuspending(bytes)
}


fun ImmutableImage.forCoWriter(writer: CoImageWriter): CoWriteContext =
    CoWriteContext(writer, this, this.metadata)


inline fun ImmutableImage.useGraphics(action: (graphics: Graphics2D) -> Unit) {
    val graphics = this.awt().createGraphics()
    try {
        action(graphics)
    } finally {
        graphics.dispose()
    }
}
