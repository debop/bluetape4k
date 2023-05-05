package io.bluetape4k.infra.graphql.dgs.scalar

import graphql.schema.Coercing
import io.bluetape4k.logging.KLogging
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ZonedDateTimeScalar: Coercing<ZonedDateTime, String> {

    companion object: KLogging() {
        @JvmField
        val IsoZonedDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    }

    override fun serialize(dataFetcherResult: Any): String = when (dataFetcherResult) {
        is ZonedDateTime -> dataFetcherResult.format(IsoZonedDateTimeFormatter)
        else             -> throw IllegalArgumentException("Not a valid java.time.ZonedDateTime [$dataFetcherResult]")
    }

    override fun parseValue(input: Any): ZonedDateTime {
        return ZonedDateTime.parse(input.toString(), IsoZonedDateTimeFormatter)
    }

    override fun parseLiteral(input: Any): ZonedDateTime {
        return ZonedDateTime.parse(input.toString(), IsoZonedDateTimeFormatter)
    }
}
