package io.bluetape4k.csv.coroutines

import com.univocity.parsers.common.record.Record
import com.univocity.parsers.csv.CsvParser
import com.univocity.parsers.csv.CsvParserSettings
import io.bluetape4k.csv.DefaultCsvParserSettings
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.InputStream
import java.nio.charset.Charset

/**
 * Co csv record reader
 *
 * @property settings
 * @constructor Create empty Co csv record reader
 */
class CoCsvRecordReader(
    private val settings: CsvParserSettings = DefaultCsvParserSettings,
): CoRecordReader {

    companion object: KLogging()

    override fun <T: Any> read(
        input: InputStream,
        encoding: Charset,
        skipHeaders: Boolean,
        recordMapper: (Record) -> T,
    ): Flow<T> = flow {
        val parser = CsvParser(settings)
        parser.iterateRecords(input, encoding)
            .drop(if (skipHeaders) 1 else 0)
            .forEach { record ->
                emit(recordMapper(record))
            }
    }

    override fun close() {
        // Nothing to do.
    }
}
