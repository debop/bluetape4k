package io.bluetape4k.workshop.es.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.bluetape4k.io.json.jackson.Jackson
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class JacksonConfig {

    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        return Jackson.defaultJsonMapper
    }
}
