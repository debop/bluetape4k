package io.bluetape4k.workshop.webflux.hibernate.reactive.config

import io.bluetape4k.logging.KLogging
import io.bluetape4k.testcontainers.jdbc.MySQL8Server
import org.hibernate.tool.schema.Action
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

@Configuration
class HibernateReactiveConfiguration {

    companion object: KLogging()

    @Bean
    fun entityManagerFactory(): EntityManagerFactory {
        return Persistence.createEntityManagerFactory("default", hibernateProperties)
    }

    private val hibernateProperties: Map<String, Any?> by lazy {
        mutableMapOf<String, Any?>(
            "javax.persistence.jdbc.url" to MySQL8Server.Launcher.mysql.jdbcUrl,
            "javax.persistence.jdbc.user" to MySQL8Server.Launcher.mysql.username,
            "javax.persistence.jdbc.password" to MySQL8Server.Launcher.mysql.password,
            "javax.persistence.schema-generation.database.action" to Action.CREATE.externalJpaName,
        )
    }
}
