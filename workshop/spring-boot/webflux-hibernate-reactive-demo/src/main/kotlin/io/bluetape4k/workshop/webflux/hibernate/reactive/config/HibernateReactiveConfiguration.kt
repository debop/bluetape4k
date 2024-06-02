package io.bluetape4k.workshop.webflux.hibernate.reactive.config

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.jdbc.MySQL8Server
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import org.hibernate.tool.schema.Action
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HibernateReactiveConfiguration {

    companion object: KLogging()

    @Bean
    fun entityManagerFactory(): EntityManagerFactory {
        return Persistence.createEntityManagerFactory("default", hibernateProperties)
    }

    private val hibernateProperties: Map<String, Any?> by lazy {
        mutableMapOf<String, Any?>(
            "jakarta.persistence.jdbc.url" to MySQL8Server.Launcher.mysql.jdbcUrl,
            "jakarta.persistence.jdbc.user" to MySQL8Server.Launcher.mysql.username,
            "jakarta.persistence.jdbc.password" to MySQL8Server.Launcher.mysql.password,
            "jakarta.persistence.schema-generation.database.action" to Action.CREATE.externalJpaName,
        )
    }
}
