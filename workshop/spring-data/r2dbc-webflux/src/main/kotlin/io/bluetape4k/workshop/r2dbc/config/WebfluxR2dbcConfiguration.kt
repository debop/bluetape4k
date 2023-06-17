package io.bluetape4k.workshop.r2dbc.config

import io.bluetape4k.data.r2dbc.connection.init.resourceDatabasePopulatorOf
import io.bluetape4k.workshop.r2dbc.handler.UserHandler
import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.http.MediaType
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.web.reactive.function.server.coRouter

@Configuration
@EnableR2dbcRepositories
class WebfluxR2dbcConfiguration {

    @Bean
    fun userRoute(userHandler: UserHandler) = coRouter {
        accept(MediaType.APPLICATION_JSON).nest {
            GET("/users", userHandler::findAll)
            GET("/users/search", userHandler::search)
            GET("/users/{id}", userHandler::findUser)
            POST("/users", userHandler::addUser)
            PUT("/users/{id}", userHandler::updateUser)
            DELETE("/users/{id}", userHandler::deleteUser)
        }
    }

    @Bean
    fun initializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        return ConnectionFactoryInitializer().apply {
            setConnectionFactory(connectionFactory)
            val populator = CompositeDatabasePopulator().apply {
                addPopulators(resourceDatabasePopulatorOf(ClassPathResource("data/schema.sql")))
                addPopulators(resourceDatabasePopulatorOf(ClassPathResource("data/data.sql")))
            }
            setDatabasePopulator(populator)
        }
    }
}
