package io.bluetape4k.utils.idgenerators.snowflake

object Snowflakers {

    val Default: Snowflake by lazy { DefaultSnowflake() }

    val Global: Snowflake by lazy { GlobalSnowflake() }

}
