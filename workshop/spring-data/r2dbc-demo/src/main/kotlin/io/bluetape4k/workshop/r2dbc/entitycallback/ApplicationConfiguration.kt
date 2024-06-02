package io.bluetape4k.workshop.r2dbc.entitycallback

import io.bluetape4k.r2dbc.connection.init.connectionFactoryInitializer
import io.bluetape4k.support.asLong
import io.bluetape4k.support.toUtf8Bytes
import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Mono

@SpringBootApplication
@EnableR2dbcRepositories(basePackageClasses = [CustomerRepository::class])
class ApplicationConfiguration {

    /**
     * Auto increment identifier 를 callback 으로 받아서 저장한 [Customer]의 Id 값에 적용합니다.
     *
     * @param databaseClient
     * @return
     */
    @Bean
    fun idGeneratingCallback(databaseClient: DatabaseClient): BeforeConvertCallback<Customer> =
        BeforeConvertCallback { customer, _ ->
            if (customer.id == null) {
                databaseClient.sql("SELECT NEXT VALUE FOR primary_key")
                    .map { readable -> readable[0].asLong() }
                    .first()
                    .map { id -> customer.withId(id) }
            } else {
                Mono.just(customer)
            }
        }

    /**
     * Database 초기화 함수
     *
     * @param connectionFactory [ConnectionFactory] 인스턴스
     * @return [ConnectionFactoryInitializer] 인스턴스
     *
     * @see [ResourceDatabasePopulator]
     * @see [org.springframework.r2dbc.connection.init.CompositeDatabasePopulator]
     * @see [org.springframework.core.io.ClassPathResource]
     */
    @Bean
    fun initializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        val sql = """
            CREATE SEQUENCE primary_key;
            DROP TABLE IF EXISTS customer;
            CREATE TABLE customer (
                id BIGINT PRIMARY KEY,
                firstname VARCHAR(100) NOT NULL,
                lastname VARCHAR(100) NOT NULL
            );
        """.trimIndent()

        return connectionFactoryInitializer(connectionFactory) {
            setDatabasePopulator(ResourceDatabasePopulator(ByteArrayResource(sql.toUtf8Bytes())))
        }
    }
}
