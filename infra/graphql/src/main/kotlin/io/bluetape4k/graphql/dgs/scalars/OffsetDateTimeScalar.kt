package io.bluetape4k.graphql.dgs.scalars

import com.netflix.graphql.dgs.DgsScalar
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import io.bluetape4k.logging.KLogging
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@DgsScalar(name = "OffsetDateTime")
class OffsetDateTimeScalar: Coercing<OffsetDateTime, String> {

    companion object: KLogging() {
        @JvmField
        val IsoOffsetDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        @JvmStatic
        val INSTANCE: GraphQLScalarType by lazy {
            GraphQLScalarType.newScalar()
                .name("OffsetDateTime")
                .description("Java 8 OffsetDateTime")
                .coercing(OffsetDateTimeScalar())
                .build()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun serialize(dataFetcherResult: Any): String = when (dataFetcherResult) {
        is OffsetDateTime -> dataFetcherResult.format(IsoOffsetDateTimeFormatter)
        else              -> throw IllegalArgumentException("Not a valid java.time.OffsetDateTime [$dataFetcherResult]")
    }

    @Deprecated("Deprecated in Java")
    override fun parseValue(input: Any): OffsetDateTime {
        return OffsetDateTime.parse(input.toString(), IsoOffsetDateTimeFormatter)
    }

    @Deprecated("Deprecated in Java")
    override fun parseLiteral(input: Any): OffsetDateTime {
        return OffsetDateTime.parse(input.toString(), IsoOffsetDateTimeFormatter)
    }
}
