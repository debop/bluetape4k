package io.bluetape4k.workshop.security.server.infrastructure

import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
@EnableMongoAuditing
@EnableReactiveMongoRepositories
class MongoConfiguration
