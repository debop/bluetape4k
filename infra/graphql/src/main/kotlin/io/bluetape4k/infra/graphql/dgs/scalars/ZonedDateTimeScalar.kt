package io.bluetape4k.infra.graphql.dgs.scalars

import com.netflix.graphql.dgs.DgsScalar
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import io.bluetape4k.logging.KLogging
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@DgsScalar(name = "ZonedDateTime")
class ZonedDateTimeScalar: Coercing<ZonedDateTime, String> {

    companion object: KLogging() {
        @JvmField
        val IsoZonedDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

        @JvmStatic
        val INSTANCE: GraphQLScalarType by lazy {
            GraphQLScalarType.newScalar()
                .name("ZonedDateTime")
                .description("Java 8 ZonedDateTime")
                .coercing(ZonedDateTimeScalar())
                .build()
        }
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
