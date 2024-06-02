package io.bluetape4k.workshop.graphql.dgs.scalars

import com.netflix.graphql.dgs.DgsScalar
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.Coercing
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug

/**
 * Custom scalar type 인 [DateRange] 를 DGS Framework에 등록합니다.
 *
 * NOTE: DGS Framework 5.5.1 에서는 Request 정보의 custom scalar type을 직렬화 할 때 `serialize` 을 사용한다.
 */
@DgsScalar(name = "DateRange")
class DateRangeScalar: Coercing<DateRange, String> {

    companion object: KLogging()

    @Deprecated("Deprecated in Java")
    override fun serialize(dataFetcherResult: Any): String {
        log.debug { "serialize dataFetcherResult: $dataFetcherResult" }
        val range = dataFetcherResult as DateRange
        return range.toIsoDateString()
    }

    @Deprecated("Deprecated in Java")
    override fun valueToLiteral(input: Any): Value<out Value<*>> {
        return StringValue((input as DateRange).toIsoDateString())
    }

    @Deprecated("Deprecated in Java")
    override fun parseValue(input: Any): DateRange {
        log.debug { "parse value: $input" }
        return DateRange.parse(input.toString())
    }

    @Deprecated("Deprecated in Java")
    override fun parseLiteral(input: Any): DateRange {
        log.debug { "parse literal: $input" }

        if (input !is StringValue) {
            throw IllegalArgumentException("DateRangeScalar can only parse string values. input=$input")
        }
        return DateRange.parse(input.value)
    }
}
