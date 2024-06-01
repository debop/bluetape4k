package io.bluetape4k.graphql.dgs.scalars

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

    @Deprecated("Deprecated in Java")
    override fun serialize(dataFetcherResult: Any): String = when (dataFetcherResult) {
        is ZonedDateTime -> dataFetcherResult.format(IsoZonedDateTimeFormatter)
        else             -> throw IllegalArgumentException("Not a valid java.time.ZonedDateTime [$dataFetcherResult]")
    }

    @Deprecated("Deprecated in Java")
    override fun parseValue(input: Any): ZonedDateTime {
        return ZonedDateTime.parse(input.toString(), IsoZonedDateTimeFormatter)
    }

    @Deprecated("Deprecated in Java")
    override fun parseLiteral(input: Any): ZonedDateTime {
        return ZonedDateTime.parse(input.toString(), IsoZonedDateTimeFormatter)
    }
}
