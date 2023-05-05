package io.bluetape4k.infra.graphql.dgs.scalar

import com.netflix.graphql.dgs.DgsScalar
import graphql.schema.Coercing
import io.bluetape4k.logging.KLogging
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DgsScalar(name = "LocalDateTime")
class LocalDateTimeScalar: Coercing<LocalDateTime, String> {

    companion object: KLogging() {
        @JvmField
        val IsoLocalDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }

    override fun serialize(dataFetcherResult: Any): String = when (dataFetcherResult) {
        is LocalDateTime -> dataFetcherResult.format(IsoLocalDateTimeFormatter)
        else             -> throw IllegalArgumentException("Not a valid java.time.LocalDateTime [$dataFetcherResult]")
    }

    override fun parseValue(input: Any): LocalDateTime {
        return LocalDateTime.parse(input.toString(), IsoLocalDateTimeFormatter)
    }

    override fun parseLiteral(input: Any): LocalDateTime {
        return LocalDateTime.parse(input.toString(), IsoLocalDateTimeFormatter)
    }
}
