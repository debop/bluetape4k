package io.bluetape4k.graphql.dgs.scalars

import com.netflix.graphql.dgs.DgsScalar
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import io.bluetape4k.logging.KLogging
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DgsScalar(name = "LocalDateTime")
class LocalDateTimeScalar: Coercing<LocalDateTime, String> {

    companion object: KLogging() {
        @JvmField
        val IsoLocalDateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

        @JvmStatic
        val INSTANCE: GraphQLScalarType by lazy {
            GraphQLScalarType.newScalar()
                .name("LocalDateTime")
                .description("Java 8 LocalDateTime")
                .coercing(LocalDateTimeScalar())
                .build()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun serialize(dataFetcherResult: Any): String = when (dataFetcherResult) {
        is LocalDateTime -> dataFetcherResult.format(IsoLocalDateTimeFormatter)
        else             -> throw IllegalArgumentException("Not a valid java.time.LocalDateTime [$dataFetcherResult]")
    }

    @Deprecated("Deprecated in Java")
    override fun parseValue(input: Any): LocalDateTime {
        return LocalDateTime.parse(input.toString(), IsoLocalDateTimeFormatter)
    }

    @Deprecated("Deprecated in Java")
    override fun parseLiteral(input: Any): LocalDateTime {
        return LocalDateTime.parse(input.toString(), IsoLocalDateTimeFormatter)
    }
}
