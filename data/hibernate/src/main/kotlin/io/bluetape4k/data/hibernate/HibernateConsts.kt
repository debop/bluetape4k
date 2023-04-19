package io.bluetape4k.data.hibernate

import java.util.Properties
import org.hibernate.cfg.AvailableSettings

object HibernateConsts {

    val DefaultJpaProperties: Properties by lazy {
        Properties().apply {
            put(AvailableSettings.HBM2DDL_AUTO, "none")

            put(AvailableSettings.POOL_SIZE, 30)
            put(AvailableSettings.SHOW_SQL, true)
            put(AvailableSettings.FORMAT_SQL, true)
        }
    }
}
