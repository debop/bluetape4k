package io.bluetape4k.data.r2dbc

import io.bluetape4k.data.r2dbc.config.R2dbcClientAutoConfiguration
import io.bluetape4k.logging.KLogging
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@ImportAutoConfiguration(classes = [R2dbcClientAutoConfiguration::class])
class R2dbcTestApplication {

    companion object: KLogging()

}
