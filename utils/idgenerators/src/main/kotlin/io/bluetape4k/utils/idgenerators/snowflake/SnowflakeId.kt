package io.bluetape4k.utils.idgenerators.snowflake

import io.bluetape4k.utils.idgenerators.ALPHA_NUMERIC_BASE
import java.io.Serializable

data class SnowflakeId(
    val timestamp: Long,
    val machineId: Int,
    val sequence: Int,
): Serializable {

    /**
     * Snowflake Id 값
     */
    val value: Long by lazy { makeId(timestamp, machineId, sequence) }

    /**
     * Snowflake Id 값을 36진수로 표현한 문자열
     */
    val valueAsString: String by lazy { value.toString(ALPHA_NUMERIC_BASE) }
}
