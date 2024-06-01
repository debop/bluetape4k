package io.bluetape4k.graphql.dgs.scalars

import com.netflix.graphql.dgs.DgsScalar
import graphql.schema.Coercing
import io.bluetape4k.support.asLongOrNull

@DgsScalar(name = "Long")
class LongScalar: Coercing<Long, Long> {

    @Deprecated("Deprecated in Java")
    override fun serialize(dataFetcherResult: Any): Long {
        return dataFetcherResult.asLongOrNull()
            ?: throw IllegalArgumentException("Not a valid Long [$dataFetcherResult]")
    }

    @Deprecated("Deprecated in Java")
    override fun parseValue(input: Any): Long {
        return input.asLongOrNull()
            ?: throw IllegalArgumentException("Fail to parse value[$input] as long.")
    }

    @Deprecated("Deprecated in Java")
    override fun parseLiteral(input: Any): Long {
        return input.asLongOrNull()
            ?: throw IllegalArgumentException("Fail to parse literal value[$input] as Long.")
    }


}
