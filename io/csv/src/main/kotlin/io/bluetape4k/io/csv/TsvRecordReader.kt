package io.bluetape4k.io.csv

import com.univocity.parsers.common.record.Record
import com.univocity.parsers.tsv.TsvParser
import com.univocity.parsers.tsv.TsvParserSettings
import io.bluetape4k.logging.KLogging
import java.io.InputStream
import java.nio.charset.Charset

/**
 * TSV 포맷의 파일을 읽어드리는 [RecordReader] 입니다.
 */
class TsvRecordReader(
    private val settings: TsvParserSettings = DefaultTsvParserSettings,
): RecordReader {

    companion object: KLogging()

    override fun <T> read(
        input: InputStream,
        encoding: Charset,
        skipHeaders: Boolean,
        recordMapper: (Record) -> T,
    ): Sequence<T> = sequence {
        val parser = TsvParser(settings)
        parser.iterateRecords(input, encoding)
            .drop(if (skipHeaders) 1 else 0)
            .forEach { record ->
                runCatching { recordMapper(record) }.onSuccess { yield(it) }
            }
    }
}
