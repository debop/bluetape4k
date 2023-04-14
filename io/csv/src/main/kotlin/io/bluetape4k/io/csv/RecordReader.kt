package io.bluetape4k.io.csv

import com.univocity.parsers.common.record.Record
import java.io.Closeable
import java.io.InputStream
import java.nio.charset.Charset

/**
 * CSV 포맷 형태의 파일 정보를 [Record] 로 읽어드리는 Reader 입니다.
 */
interface RecordReader: Closeable {

    fun <T> read(
        input: InputStream,
        encoding: Charset = Charsets.UTF_8,
        skipHeaders: Boolean = true,
        recordMapper: (Record) -> T,
    ): Sequence<T>

    fun read(
        input: InputStream,
        encoding: Charset = Charsets.UTF_8,
        skipHeaders: Boolean = true,
    ): Sequence<Record> {
        return read(input, encoding, skipHeaders) { it }
    }

    override fun close() {
        // NOP
    }
}
