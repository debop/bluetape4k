package io.bluetape4k.data.r2dbc.convert

import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter

inline fun <reified T> MappingR2dbcConverter.read(row: Row, metadata: RowMetadata? = null): T =
    read(T::class.java, row, metadata)

inline fun <reified T> MappingR2dbcConverter.getTargetType(): Class<*> = getTargetType(T::class.java)
