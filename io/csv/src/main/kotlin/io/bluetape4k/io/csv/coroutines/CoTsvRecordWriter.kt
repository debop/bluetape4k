package io.bluetape4k.io.csv.coroutines

import com.univocity.parsers.tsv.TsvWriter
import com.univocity.parsers.tsv.TsvWriterSettings
import io.bluetape4k.io.csv.DefaultTsvWriterSettings
import io.bluetape4k.logging.KLogging
import java.io.Writer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlin.coroutines.CoroutineContext

class CoTsvRecordWriter private constructor(
    private val writer: TsvWriter,
): CoRecordWriter {

    companion object: KLogging() {
        operator fun invoke(writer: TsvWriter): CoTsvRecordWriter {
            return CoTsvRecordWriter(writer)
        }

        operator fun invoke(
            writer: Writer,
            settings: TsvWriterSettings = DefaultTsvWriterSettings,
        ): CoTsvRecordWriter {
            return CoTsvRecordWriter(TsvWriter(writer, settings))
        }
    }

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override suspend fun writeHeaders(headers: Iterable<String>) {
        writer.writeHeaders(headers.toList())
    }

    override suspend fun writeRow(row: Iterable<*>) {
        writer.writeRow(row.toList())
    }

    override suspend fun writeAll(rows: Sequence<Iterable<*>>) {
        rows.forEach { writeRow(it) }
    }

    override suspend fun writeAll(rows: Flow<Iterable<*>>) {
        rows.buffer().collect { writeRow(it) }
    }

    override fun close() {
        job.cancelChildren()
        job.cancel()
        writer.close()
    }
}
