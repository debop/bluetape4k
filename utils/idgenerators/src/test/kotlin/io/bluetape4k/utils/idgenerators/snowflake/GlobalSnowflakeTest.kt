package io.bluetape4k.utils.idgenerators.snowflake

class GlobalSnowflakeTest : AbstractSnowflakeTest() {

    override val snowflake: Snowflake = GlobalSnowflake()

}
