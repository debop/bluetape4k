package io.bluetape4k.infra.graphql.dgs.scalar

import com.netflix.graphql.dgs.DgsScalar
import graphql.schema.Coercing
import graphql.schema.CoercingParseValueException
import io.bluetape4k.support.asLongOrNull
import java.time.Instant

@DgsScalar(name = "Instant")
class InstantScalar: Coercing<Instant, Long> {

    override fun serialize(dataFetcherResult: Any): Long {
        return when (dataFetcherResult) {
            is Instant -> dataFetcherResult.toEpochMilli()
            else       -> throw IllegalArgumentException("Not a valid java.timme.Instant [$dataFetcherResult]")
        }
    }

    override fun parseValue(input: Any): Instant {
        val epochMilli = input.asLongOrNull()
            ?: throw CoercingParseValueException("Fail to parse value[$input] as Long")

        return Instant.ofEpochMilli(epochMilli)
    }

    override fun parseLiteral(input: Any): Instant {
        val epochMilli = input.asLongOrNull()
            ?: throw CoercingParseValueException("Fail to parse literal value[$input] as Long")

        return Instant.ofEpochMilli(epochMilli)
    }
}
