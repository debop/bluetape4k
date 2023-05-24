package io.bluetape4k.utils.idgenerators.snowflake

import io.bluetape4k.utils.idgenerators.LongIdGenerator

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
