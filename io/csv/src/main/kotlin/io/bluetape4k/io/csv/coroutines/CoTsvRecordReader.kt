package io.bluetape4k.io.csv.coroutines

import com.univocity.parsers.common.record.Record
import com.univocity.parsers.tsv.TsvParser
import com.univocity.parsers.tsv.TsvParserSettings
import io.bluetape4k.io.csv.DefaultTsvParserSettings
import io.bluetape4k.logging.KLogging
import java.io.InputStream
import java.nio.charset.Charset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext

/**
 * Coroutines 환경 하에서 TSV 포맷의 Record 를 읽어드립니다.
 *
 * @property settings
 */
class CoTsvRecordReader(
    private val settings: TsvParserSettings = DefaultTsvParserSettings,
): CoRecordReader {

    companion object: KLogging()

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun close() {
        job.cancelChildren()
        job.cancel()
    }

    override fun <T: Any> read(
        input: InputStream,
        encoding: Charset,
        skipHeaders: Boolean,
        recordMapper: (Record) -> T,
    ): Flow<T> {
        val parser = TsvParser(settings)

        return flow {
            parser.iterateRecords(input, encoding)
                .drop(if (skipHeaders) 1 else 0)
                .forEach { record ->
                    emit(recordMapper(record))
                }
        }
    }
}
