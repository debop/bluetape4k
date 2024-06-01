package io.bluetape4k.csv

import com.univocity.parsers.csv.CsvParserSettings
import com.univocity.parsers.csv.CsvWriterSettings
import com.univocity.parsers.tsv.TsvParserSettings
import com.univocity.parsers.tsv.TsvWriterSettings


const val MAX_CHARS_PER_COLUMN: Int = 100_000

@JvmField
val DefaultCsvParserSettings: CsvParserSettings = CsvParserSettings().apply {
    trimValues(true)
    maxCharsPerColumn = MAX_CHARS_PER_COLUMN
}

@JvmField
val DefaultTsvParserSettings: TsvParserSettings = TsvParserSettings().apply {
    trimValues(true)
    maxCharsPerColumn = MAX_CHARS_PER_COLUMN
}

@JvmField
val DefaultCsvWriterSettings: CsvWriterSettings = CsvWriterSettings().apply {
    maxCharsPerColumn = MAX_CHARS_PER_COLUMN
}

@JvmField
val DefaultTsvWriterSettings: TsvWriterSettings = TsvWriterSettings().apply {
    maxCharsPerColumn = MAX_CHARS_PER_COLUMN
}
