package io.bluetape4k.infra.graphql.dgs.scalar

import graphql.schema.Coercing
import io.bluetape4k.logging.KLogging
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class OffsetDateTimeScalar: Coercing<OffsetDateTime, String> {

    companion object: KLogging() {
        @JvmField
        val IsoOffsetDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
    }

    override fun serialize(dataFetcherResult: Any): String = when (dataFetcherResult) {
        is OffsetDateTime -> dataFetcherResult.format(IsoOffsetDateTimeFormatter)
        else              -> throw IllegalArgumentException("Not a valid java.time.OffsetDateTime [$dataFetcherResult]")
    }

    override fun parseValue(input: Any): OffsetDateTime {
        return OffsetDateTime.parse(input.toString(), IsoOffsetDateTimeFormatter)
    }

    override fun parseLiteral(input: Any): OffsetDateTime {
        return OffsetDateTime.parse(input.toString(), IsoOffsetDateTimeFormatter)
    }
}
