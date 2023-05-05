package io.bluetape4k.io.csv

import com.univocity.parsers.tsv.TsvWriter
import com.univocity.parsers.tsv.TsvWriterSettings
import io.bluetape4k.logging.KLogging
import java.io.Writer

/**
 * TSV 포맷으로 데이터를 파일로 쓰는 [RecordWriter] 입니다.
 *
 * @property writer
 */
class TsvRecordWriter private constructor(
    private val writer: TsvWriter,
): RecordWriter {

    companion object: KLogging() {
        operator fun invoke(tsvWriter: TsvWriter): TsvRecordWriter {
            return TsvRecordWriter(tsvWriter)
        }

        operator fun invoke(
            writer: Writer,
            settings: TsvWriterSettings = DefaultTsvWriterSettings,
        ): TsvRecordWriter {
            return TsvRecordWriter(TsvWriter(writer, settings))
        }
    }

    override fun writeHeaders(headers: Iterable<String>) {
        writer.writeHeaders(headers.toList())
    }

    override fun writeRow(rows: Iterable<*>) {
        writer.writeRow(rows.toList())
    }

    override fun writeAll(rows: Sequence<Iterable<*>>) {
        rows.forEach { writeRow(it) }
    }

    override fun close() {
        runCatching { writer.close() }
    }
}
