package io.bluetape4k.io.csv.coroutines

import com.univocity.parsers.tsv.TsvWriter
import com.univocity.parsers.tsv.TsvWriterSettings
import io.bluetape4k.io.csv.DefaultTsvWriterSettings
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import java.io.Writer

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
        runCatching { writer.close() }
    }
}
