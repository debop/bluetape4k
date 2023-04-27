package io.bluetape4k.io.csv

import com.univocity.parsers.common.record.Record
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.nio.charset.Charset
import kotlin.text.Charsets.UTF_8

fun File.readAsCsvRecords(
    cs: Charset = UTF_8,
    skipHeader: Boolean = true,
): Sequence<Record> {
    return FileInputStream(this).buffered().use { inputStream ->
        inputStream.readAsCsvRecords(cs, skipHeader)
    }
}

fun File.readAsTsvRecords(
    cs: Charset = UTF_8,
    skipHeader: Boolean = true,
): Sequence<Record> {
    return FileInputStream(this).buffered().use { inputStream ->
        inputStream.readAsTsvRecords(cs, skipHeader)
    }
}

fun InputStream.readAsCsvRecords(
    cs: Charset = UTF_8,
    skipHeader: Boolean = true,
): Sequence<Record> = sequence {
    CsvRecordReader().read(this@readAsCsvRecords, cs, skipHeader) { it }
}

fun InputStream.readAsTsvRecords(
    cs: Charset = UTF_8,
    skipHeader: Boolean = true,
): Sequence<Record> = sequence {
    TsvRecordReader().read(this@readAsTsvRecords, cs, skipHeader) { it }
}
