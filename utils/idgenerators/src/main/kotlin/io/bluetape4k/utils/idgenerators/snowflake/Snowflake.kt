package io.bluetape4k.utils.idgenerators.snowflake

import io.bluetape4k.core.assertNotBlank
import io.bluetape4k.utils.idgenerators.ALPHA_NUMERIC_BASE
import io.bluetape4k.utils.idgenerators.IdGenerator
import io.bluetape4k.utils.idgenerators.parseAsLong

/**
 * Twitter Snowflake 알고리즘을 이용하여 Time 기반의 Long 수형의 Unique Id 를 생성합니다.
 */
interface Snowflake: IdGenerator<Long> {

    override fun nextIdAsString(): String = nextId().toString(ALPHA_NUMERIC_BASE)

    fun parse(id: Long): SnowflakeId = parseSnowflakeId(id)

    fun parse(idString: String): SnowflakeId {
        idString.assertNotBlank("idString")

        val id = idString.parseAsLong()
        return parseSnowflakeId(id)
    }
}
