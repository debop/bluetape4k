package io.bluetape4k.io.csv.coroutines

import com.univocity.parsers.common.record.Record
import com.univocity.parsers.tsv.TsvParser
import com.univocity.parsers.tsv.TsvParserSettings
import io.bluetape4k.io.csv.DefaultTsvParserSettings
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.InputStream
import java.nio.charset.Charset

/**
 * Coroutines 환경 하에서 TSV 포맷의 Record 를 읽어드립니다.
 *
 * @property settings
 */
class CoTsvRecordReader(
    private val settings: TsvParserSettings = DefaultTsvParserSettings,
): CoRecordReader {

    companion object: KLogging()

    override fun <T: Any> read(
        input: InputStream,
        encoding: Charset,
        skipHeaders: Boolean,
        recordMapper: (Record) -> T,
    ): Flow<T> = flow {
        val parser = TsvParser(settings)

        parser.iterateRecords(input, encoding)
            .drop(if (skipHeaders) 1 else 0)
            .forEach { record ->
                runCatching { recordMapper(record) }.onSuccess { emit(it) }
            }
    }

    override fun close() {
        // 뭘 close 해야지?

        // Nothing to do
    }
}
