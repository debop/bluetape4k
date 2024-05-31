package io.bluetape4k.io

import io.bluetape4k.concurrent.asCompletableFuture
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
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.concurrent.CompletableFuture
import kotlin.text.Charsets.UTF_8

private val log by lazy { KotlinLogging.logger {} }

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
    bufferSize: Int = kotlin.io.DEFAULT_BUFFER_SIZE,
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
fun File.touch(): Boolean {
    if (!exists()) {
        FileOutputStream(this).closeSafe()
    }
    return setLastModified(System.currentTimeMillis())
}

fun File.readAllBytes(): ByteArray {
    if (!exists()) {
        return emptyByteArray
    }
    return this.readBytes()
}

/**
 * 해당 경로[Path]의 파일을 비동기 방식으로 읽어 [ByteArray]로 반환하는 [CompletableFuture]를 반환합니다.
 */
fun Path.readAllBytesAsync(): CompletableFuture<ByteArray> {
    val promise = CompletableFuture<ByteArray>()
    val channel = AsynchronousFileChannel.open(this, StandardOpenOption.READ)
    val buffer = ByteBuffer.allocateDirect(channel.size().toInt())

    channel.read(buffer, 0).asCompletableFuture()
        .whenCompleteAsync { result, error ->
            if (error != null) {
                channel.closeSafe()
                promise.completeExceptionally(error)
            } else {
                try {
                    if (result != null) {
                        buffer.flip()
                        promise.complete(buffer.getBytes())
                        log.trace { "Read bytearray from file. path=[${this@readAllBytesAsync}], read size=$result" }
                    } else {
                        promise.complete(emptyByteArray)
                    }
                } finally {
                    channel.closeSafe()
                }
            }
        }
    return promise
}

fun File.readLineSequence(cs: Charset = UTF_8): Sequence<String> =
    FileInputStream(this).toLineSequence(cs)

fun File.readAllLines(cs: Charset = UTF_8): List<String> =
    FileInputStream(this).toStringList(cs)

fun Path.readAllLinesAsync(cs: Charset = UTF_8): CompletableFuture<List<String>> =
    readAllBytesAsync().thenApplyAsync { it.toString(cs).lines() }

fun File.write(bytes: ByteArray, append: Boolean = false) {
    FileUtils.writeByteArrayToFile(this, bytes, append)
}

fun File.writeLines(lines: Collection<String>, append: Boolean = false, cs: Charset = UTF_8) {
    FileUtils.writeLines(this, cs.name(), lines, append)
}

/**
 * 지정한 경로[Path]에 [bytes] 내용을 비동기 방식으로 쓰고, 쓰기 완료 후 쓰여진 바이트 수를 반환하는 [CompletableFuture]를 반환합니다.
 *
 * @param bytes 파일에 쓸 [ByteArray]
 * @param append 기존 파일에 추가할 것인가 여부
 * @return 파일에 쓰여진 바이트 수를 반환하는 [CompletableFuture]
 */
fun Path.writeAsync(bytes: ByteArray, append: Boolean = false): CompletableFuture<Long> {
    val promise = CompletableFuture<Long>()

    val options = arrayOf(StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    val channel = AsynchronousFileChannel.open(this, *options)

    val pos = if (append) channel.size() else 0L
    val content = bytes.toByteBufferDirect()

    channel.write(content, pos).asCompletableFuture()
        .whenCompleteAsync { result, error ->
            if (error != null) {
                channel.closeSafe()
                promise.completeExceptionally(error)
            } else {
                promise.complete(result.toLong())
                log.trace { "Write bytearray to file. path=[${this@writeAsync}], written size=$result" }
                channel.closeSafe()
            }
        }
    return promise
}

/**
 * 지정한 경로[Path]에 [lines] 내용을 비동기 방식으로 쓰고, 쓰기 완료 후 쓰여진 바이트 수를 반환하는 [CompletableFuture]를 반환합니다.
 *
 * @param lines 파일에 쓸 라인
 * @param append 기존 파일에 추가할 것인가 여부
 * @param cs Charset
 * @return 파일에 쓰여진 바이트 수를 반환하는 [CompletableFuture]
 */
fun Path.writeLinesAsync(
    lines: Iterable<String>,
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
fun Path.bufferedReader(cs: Charset = UTF_8, bufferSize: Int = kotlin.io.DEFAULT_BUFFER_SIZE) =
    InputStreamReader(FileInputStream(this.toFile()), cs).buffered(bufferSize)

/**
 * Path의 파일에 데이터를 쓰기 위한 [BufferedWriter]를 빌드합니다.
 * @receiver Path
 * @param cs Charset
 * @param bufferSize Int
 * @return BufferedWriter
 */
fun Path.bufferedWriter(cs: Charset = UTF_8, bufferSize: Int = kotlin.io.DEFAULT_BUFFER_SIZE): BufferedWriter =
    OutputStreamWriter(FileOutputStream(this.toFile()), cs).buffered(bufferSize)

/**
 * File을 읽어 [ByteArray] 로 빌드합니다.
 */
fun File.toByteArray(): ByteArray = Files.newInputStream(this.toPath()).readBytes()
