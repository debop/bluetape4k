package io.bluetape4k.io

import kotlinx.coroutines.future.await
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Path


/**
 * [Path]의 모든 바이트를 읽어옵니다.
 *
 * @return ByteArray 파일의 모든 바이트
 */
suspend fun Path.readAllBytesSuspending(): ByteArray = readAllBytesAsync().await()

/**
 * [File]의 모든 바이트를 읽어옵니다.
 *
 * @return ByteArray 파일의 모든 바이트
 */
suspend fun File.readAllBytesSuspending(): ByteArray = toPath().readAllBytesSuspending()

/**
 * [Path]의 모든 라인을 읽어옵니다.
 *
 * @param charset Charset
 * @return 파일의 모든 라인
 */
suspend fun Path.readAllLinesSuspending(charset: Charset = Charsets.UTF_8): List<String> {
    return readAllBytesSuspending()
        .toString(charset)
        .lineSequence()
        .toList()
}

/**
 * [Path]에 [ByteArray]를 비동기 방식으로 쓰기합니다.
 *
 * @param bytes ByteArray 파일에 쓸 내용
 * @param append Boolean  추가 여부
 * @return Long
 */
suspend fun Path.writeSuspending(bytes: ByteArray, append: Boolean = false): Long {
    return writeAsync(bytes, append).await()
}

/**
 * [Path]에 [Iterable]의 라인을 비동기 방식으로 쓰기합니다.
 *
 * @param lines Iterable<String> 파일에 쓸 라인
 * @param append Boolean 추가 여부
 * @param charset Charset Charset
 */
suspend fun Path.writeLinesSuspending(
    lines: Iterable<String>,
    append: Boolean = false,
    charset: Charset = Charsets.UTF_8,
): Long {
    return writeLinesAsync(lines, append, charset).await()
}
