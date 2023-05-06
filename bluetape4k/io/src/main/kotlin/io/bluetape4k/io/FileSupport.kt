package io.bluetape4k.io

import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.logging.error
import io.bluetape4k.logging.trace
import io.bluetape4k.support.LINE_SEPARATOR
import io.bluetape4k.support.closeSafe
import io.bluetape4k.support.emptyByteArray
import io.bluetape4k.utils.Runtimex
import org.apache.commons.io.FileUtils
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import java.nio.charset.Charset
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.concurrent.CompletableFuture
import kotlin.text.Charsets.UTF_8

private val log = KotlinLogging.logger {}

const val EXTENSION_SEPARATOR = '.'
const val UNIX_SEPARATOR = '/'
const val WINDOW_SEPARATOR = '\\'


@JvmField
val SYSTEM_SEPARATOR = File.separatorChar

/**
 * 디렉토리 생성
 * @param dir String
 * @return File?
 */
fun createDirectory(dir: String): File? {
    log.trace { "Create directory. dir=[$dir]" }

    return try {
        val file = File(dir)
        val created = file.mkdirs()

        if (created) file else null
    } catch (e: Exception) {
        log.error(e) { "Fail to create directory. dir=[$dir]" }
        null
    }
}

/**
 * 파일 경로가 없을 때에는 상위 폴더들을 생성합니다.
 */
fun File.createParentDirectory() {
    this.canonicalFile.parentFile?.run {
        mkdirs()
        if (!isDirectory) {
            throw IOException("Unable to create parent directory of ${this@createParentDirectory}")
        }
    }
}

fun createFile(path: String): File {
    log.trace { "Create file. path=[$path]" }

    val file = File(path)
    file.createParentDirectory()

    file.createNewFile()
    return file
}

/**
 * 임시 디렉토리를 생성합니다.
 * @param deleteAtExit Boolean 프로그램 종료 시 삭제할 것인가 여부
 * @return File 생성된 임시 디렉토리
 */
@JvmOverloads
fun createTempDirectory(prefix: String = "temp", suffix: String = "dir", deleteAtExit: Boolean = true): File {
    val dir = File.createTempFile(prefix, suffix)
    dir.deleteRecursively()
    dir.mkdirs()

    if (deleteAtExit) {
        Runtimex.addShutdownHook {
            dir.deleteRecursively()
        }
    }
    return dir
}

@JvmOverloads
fun File.copyToAsync(
    target: File,
    overwrite: Boolean = false,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
): CompletableFuture<File> =
    CompletableFuture.supplyAsync {
        this@copyToAsync.copyTo(target, overwrite, bufferSize)
    }

fun File.move(dest: File) {
    FileUtils.moveFile(this, dest)
}

fun File.moveAsync(dest: File): CompletableFuture<Void> =
    CompletableFuture.runAsync { this@moveAsync.move(dest) }

fun File.deleteIfExists() {
    if (this.exists()) {
        FileUtils.deleteQuietly(this)
    }
}

fun File.deleteDirectoryRecursively(): Boolean {
    if (isDirectory) {
        return deleteRecursively()
    }
    return false
}

@JvmOverloads
fun File.deleteDirectory(recusive: Boolean = true): Boolean {
    return if (recusive) {
        deleteDirectoryRecursively()
    } else {
        if (exists()) {
            FileUtils.deleteDirectory(this)
            true
        } else {
            false
        }
    }
}

/**
 * Unit "touch" utility를 구현한 함수입니다.
 * 파일이 존재하지 않는 경우 크기가 0 인 파일을 새로 만듭니다.
 */
fun File.touch() {
    if (!exists()) {
        FileOutputStream(this).close()
    }
    setLastModified(System.currentTimeMillis())
}


fun File.readAllBytes(): ByteArray {
    if (!exists()) {
        return ByteArray(0)
    }
    return this.readBytes()
}

fun Path.readAllBytesAsync(): CompletableFuture<ByteArray> {
    val future = CompletableFuture<ByteArray>()

    val channel = AsynchronousFileChannel.open(this, StandardOpenOption.READ)

    val buffer = ByteBuffer.allocateDirect(channel.size().toInt())
    val handler = object: CompletionHandler<Int?, Void?> {
        override fun completed(result: Int?, attachment: Void?) {
            if (result != null) {
                buffer.flip()
                future.complete(buffer.getBytes())
                log.trace { "Read bytearray from file. path=[${this@readAllBytesAsync}], read size=$result" }
            } else {
                future.complete(emptyByteArray)
            }
            channel.closeSafe()
        }

        override fun failed(exc: Throwable?, attachment: Void?) {
            future.completeExceptionally(exc)
            channel.closeSafe()
        }
    }

    channel.read(buffer, 0, null, handler)

    return future
}


fun File.readLineSequence(cs: Charset = UTF_8): Sequence<String> =
    FileInputStream(this).toLineSequence(cs)

fun File.readAllLines(cs: Charset = UTF_8): List<String> =
    FileInputStream(this).toStringList(cs)

fun Path.readAllLinesAsync(cs: Charset = UTF_8): CompletableFuture<List<String>> =
    readAllBytesAsync().thenApplyAsync { it.toString(cs).lines() }


@JvmOverloads
fun File.write(bytes: ByteArray, append: Boolean = false) {
    FileUtils.writeByteArrayToFile(this, bytes, append)
}

@JvmOverloads
fun File.writeLines(lines: Collection<String>, append: Boolean = false, cs: Charset = UTF_8) {
    FileUtils.writeLines(this, cs.name(), lines, append)
}

fun Path.writeAsync(bytes: ByteArray, append: Boolean = false): CompletableFuture<Long> {
    val promise = CompletableFuture<Long>()

    val options = arrayOf(StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    val channel = AsynchronousFileChannel.open(this, *options)

    val pos = if (append) channel.size() else 0L
    val content = bytes.toByteBufferDirect()

    // TODO: 이렇게 Callback 방식 이외에 Future 방식 (channel.write(content, pos)) 를 사용하면 Coroutines로 사용할 수 있다.
    val handler = object: CompletionHandler<Int, ByteBuffer?> {
        override fun completed(result: Int, attachment: ByteBuffer?) {
            promise.complete(result.toLong())
            log.trace { "Write bytearray to file. path=[${this@writeAsync}], written=$result" }
            channel.closeSafe()
        }

        override fun failed(exc: Throwable?, attachment: ByteBuffer?) {
            promise.completeExceptionally(exc)
            channel.closeSafe()
        }
    }
    channel.write(content, pos, content, handler)

    return promise
}

@JvmOverloads
fun Path.writeLinesAsync(
    lines: Collection<String>,
    append: Boolean = false,
    cs: Charset = UTF_8,
): CompletableFuture<Long> {
    val bytes = lines.joinToString(LINE_SEPARATOR).toByteArray(cs)
    return writeAsync(bytes, append)
}

/**
 * Path의 파일을 읽기 위한 [BufferedReader]를 빌드합니다.
 * @receiver Path
 * @param cs Charset
 * @param bufferSize Int
 * @return BufferedReader
 */
@JvmOverloads
fun Path.bufferedReader(cs: Charset = UTF_8, bufferSize: Int = DEFAULT_BUFFER_SIZE) =
    InputStreamReader(FileInputStream(this.toFile()), cs).buffered(bufferSize)

/**
 * Path의 파일에 데이터를 쓰기 위한 [BufferedWriter]를 빌드합니다.
 * @receiver Path
 * @param cs Charset
 * @param bufferSize Int
 * @return BufferedWriter
 */
@JvmOverloads
fun Path.bufferedWriter(cs: Charset = UTF_8, bufferSize: Int = DEFAULT_BUFFER_SIZE): BufferedWriter =
    OutputStreamWriter(FileOutputStream(this.toFile()), cs).buffered(bufferSize)
