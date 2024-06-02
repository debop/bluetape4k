package io.bluetape4k.workshop.graphql.dgs.scalars

import io.bluetape4k.logging.KLogging
import java.io.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Custom type 을 GraphQL에서 사용하기 위해서는 [DateRangeScalar]의 `@DgsScalar` annotation을 사용하여 등록해주어야 한다.
 * 또한, Custom Type을 활용한 경우 build.gradle.kts 의 dgs code generation 시에 매핑을 해주어야 합니다.
 *
 * ```
 * tasks.withType<com.netflix.graphql.dgs.codegen.gradle.GenerateJavaTask> {
 *     generateClient = true
 *     packageName = "io.bluetape4k.workshop.graphql.dgs.generated"
 *
 *     // NOTE: @DgsScalar 로 등록할 Custom type 은 Code Generation 시 Mapping 해야 합니다.
 *     typeMapping = mutableMapOf(
 *         "DateRange" to "io.bluetape4k.workshop.graphql.dgs.scalars.DateRange"
 *     )
 * }
 * ```
 *
 * @property start 시작 시각
 * @property end 종료 시각
 */
data class DateRange(
    val start: LocalDate,
    val end: LocalDate,
): Serializable {
    companion object: KLogging() {
        const val RANGE_SEPARATOR = ".."
        private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        @JvmStatic
        fun parse(literal: String): DateRange {
            val split = literal.split(RANGE_SEPARATOR, limit = 2)
            return DateRange(
                LocalDate.parse(split[0], formatter),
                LocalDate.parse(split[1], formatter)
            )
        }
    }

    fun toIsoDateString(): String {
        return start.format(formatter) + RANGE_SEPARATOR + end.format(formatter)
    }
}
