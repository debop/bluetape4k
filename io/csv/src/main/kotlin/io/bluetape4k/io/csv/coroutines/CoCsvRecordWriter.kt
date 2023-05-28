package io.bluetape4k.io.csv.coroutines

import com.univocity.parsers.csv.CsvWriter
import com.univocity.parsers.csv.CsvWriterSettings
import io.bluetape4k.io.csv.DefaultCsvWriterSettings
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import java.io.Writer

class CoCsvRecordWriter private constructor(
    private val writer: CsvWriter,
): CoRecordWriter {

    companion object: KLogging() {
        operator fun invoke(writer: CsvWriter): CoCsvRecordWriter =
            CoCsvRecordWriter(writer)

        operator fun invoke(
            writer: Writer,
            settings: CsvWriterSettings = DefaultCsvWriterSettings,
        ): CoCsvRecordWriter =
            CoCsvRecordWriter(CsvWriter(writer, settings))
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
