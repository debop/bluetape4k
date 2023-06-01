package io.bluetape4k.data.r2dbc.config

import io.bluetape4k.data.r2dbc.R2dbcClient
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.r2dbc.core.DatabaseClient

@Configuration
@ConditionalOnClass(DatabaseClient::class)
class R2dbcClientAutoConfiguration {

    companion object: KLogging()

    @Bean
//    @ConditionalOnProperty(
//        value = ["bluetape4k.data.r2dbc.autoconfigure"],
//        havingValue = "true",
//        matchIfMissing = true
//    )
    fun dbClient(
        databaseClient: DatabaseClient,
        r2dbcEntityTemplate: R2dbcEntityTemplate,
        mappingR2dbcConverter: MappingR2dbcConverter,
    ): R2dbcClient {
        log.info { "Create R2dbcClient." }
        return R2dbcClient(databaseClient, r2dbcEntityTemplate, mappingR2dbcConverter)
    }
}
