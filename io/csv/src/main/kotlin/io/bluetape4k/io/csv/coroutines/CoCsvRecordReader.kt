package io.bluetape4k.io.csv.coroutines

import com.univocity.parsers.common.record.Record
import com.univocity.parsers.csv.CsvParser
import com.univocity.parsers.csv.CsvParserSettings
import io.bluetape4k.io.csv.DefaultCsvParserSettings
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.InputStream
import java.nio.charset.Charset
import kotlin.coroutines.CoroutineContext

/**
 * Co csv record reader
 *
 * @property settings
 * @constructor Create empty Co csv record reader
 */
class CoCsvRecordReader(private val settings: CsvParserSettings = DefaultCsvParserSettings): CoRecordReader {

    companion object: KLogging()

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun <T: Any> read(
        input: InputStream,
        encoding: Charset,
        skipHeaders: Boolean,
        recordMapper: (Record) -> T,
    ): Flow<T> {
        val parser = CsvParser(settings)

        return flow {
            parser.iterateRecords(input, encoding)
                .drop(if (skipHeaders) 1 else 0)
                .forEach { record ->
                    emit(recordMapper(record))
                }
        }
    }

    override fun close() {
        job.cancelChildren()
        job.cancel()
    }
}
