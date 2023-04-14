package io.bluetape4k.io.csv.coroutines

import com.univocity.parsers.common.record.Record
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import java.io.Closeable
import java.io.InputStream
import java.nio.charset.Charset

/**
 * Coroutines 환경하에서 CSV/TSV Record를 읽는 Reader입니다.
 */
interface CoRecordReader: CoroutineScope, Closeable {

    /**
     * CSV 나 TSV 등의 파일을 읽어드립니다.
     *
     * @param input InputStream 읽어드릴 Stream
     * @param encoding Charset 인코등 정보
     * @param skipHeaders Boolean 파일에 헤더가 있다면 skip 할지 여부
     * @return Flow<Record> 읽어드린 Record를 제공하는 [Flow]
     */
    fun <T: Any> read(
        input: InputStream,
        encoding: Charset = Charsets.UTF_8,
        skipHeaders: Boolean = true,
        recordMapper: (Record) -> T,
    ): Flow<T>

    /**
     * CSV 나 TSV 등의 파일을 읽어드립니다.
     *
     * @param input InputStream 읽어드릴 Stream
     * @param encoding Charset 인코등 정보
     * @param skipHeaders Boolean 파일에 헤더가 있다면 skip 할지 여부
     * @return Flow<Record> 읽어드린 Record를 제공하는 [Flow]
     */
    fun read(
        input: InputStream,
        encoding: Charset = Charsets.UTF_8,
        skipHeaders: Boolean = true,
    ): Flow<Record> {
        return read(input, encoding, skipHeaders) { it }
    }
}
