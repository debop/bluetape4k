package io.bluetape4k.images.coroutines

import com.sksamuel.scrimage.AwtImage
import com.sksamuel.scrimage.metadata.ImageMetadata
import io.bluetape4k.io.writeSuspending
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.nio.file.Path
import java.nio.file.Paths

/**
 * 비동기 방식으로 쓰기 작업 시 사용할 Context 입니다.
 */
class CoWriteContext(
    val writer: CoImageWriter,
    private val image: AwtImage,
    private val metadata: ImageMetadata,
) {

    companion object: KLogging()

    suspend fun bytes(): ByteArray {
        ByteArrayOutputStream().use { bos ->
            writer.writeSuspending(image, metadata, bos)
            return bos.toByteArray()
        }
    }

    suspend fun stream(): ByteArrayInputStream {
        return ByteArrayInputStream(bytes())
    }

    suspend fun write(path: String): Path {
        return write(Paths.get(path))
    }

    suspend fun write(file: File): File {
        write(file.toPath())
        return file
    }

    suspend fun write(path: Path): Path {
        log.debug { "Write image to $path" }
        path.writeSuspending(bytes())
        return path
    }

    suspend fun write(out: OutputStream) {
        writer.writeSuspending(image, metadata, out)
    }
}
