package io.bluetape4k.io.csv

import com.univocity.parsers.common.record.Record
import com.univocity.parsers.csv.CsvParser
import com.univocity.parsers.csv.CsvParserSettings
import io.bluetape4k.logging.KLogging
import java.io.InputStream
import java.nio.charset.Charset

/**
 * CSV 파일 포맷을 읽어드리는 [RecordReader] 입니다.
 */
class CsvRecordReader(
    private val settings: CsvParserSettings = DefaultCsvParserSettings,
): RecordReader {

    companion object: KLogging()

    override fun <T> read(
        input: InputStream,
        encoding: Charset,
        skipHeaders: Boolean,
        recordMapper: (Record) -> T,
    ): Sequence<T> = sequence {
        CsvParser(settings).iterateRecords(input, encoding)
            .drop(if (skipHeaders) 1 else 0)
            .forEach { record ->
                yield(recordMapper(record))
            }
    }
}
