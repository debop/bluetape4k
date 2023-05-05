package io.bluetape4k.infra.graphql.dgs.scalar

import com.netflix.graphql.dgs.DgsScalar
import graphql.schema.Coercing
import io.bluetape4k.support.asLongOrNull

@DgsScalar(name = "Long")
class LongScalar: Coercing<Long, Long> {

    override fun serialize(dataFetcherResult: Any): Long {
        return dataFetcherResult.asLongOrNull()
            ?: throw IllegalArgumentException("Not a valid Long [$dataFetcherResult]")
    }

    override fun parseValue(input: Any): Long {
        return input.asLongOrNull()
            ?: throw IllegalArgumentException("Fail to parse value[$input] as long.")
    }

    override fun parseLiteral(input: Any): Long {
        return input.asLongOrNull()
            ?: throw IllegalArgumentException("Fail to parse literal value[$input] as Long.")
    }


}
