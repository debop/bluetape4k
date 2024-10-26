package io.bluetape4k.idgenerators.snowflake

import io.bluetape4k.idgenerators.LongIdGenerator

@Deprecated("use Snowflakers")
object Snowfloker {


    object Default: LongIdGenerator {
        private val default = DefaultSnowflake()

        override fun nextId(): Long = default.nextId()
    }

    object Global: LongIdGenerator {
        private val global = GlobalSnowflake()

        override fun nextId(): Long = global.nextId()
    }


}
