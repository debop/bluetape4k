package io.bluetape4k.utils.idgenerators.snowflake

import io.bluetape4k.core.assertNotBlank
import io.bluetape4k.utils.idgenerators.LongIdGenerator
import io.bluetape4k.utils.idgenerators.parseAsLong

/**
 * Twitter Snowflake 알고리즘을 이용하여 Time 기반의 Long 수형의 Unique Id 를 생성합니다.
 */
interface Snowflake : LongIdGenerator {

    fun parse(id: Long): SnowflakeId = parseSnowflakeId(id)

    fun parse(idString: String): SnowflakeId {
        idString.assertNotBlank("idString")

        val id = idString.parseAsLong()
        return parseSnowflakeId(id)
    }
}
