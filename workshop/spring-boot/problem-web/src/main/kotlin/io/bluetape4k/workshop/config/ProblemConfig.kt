package io.bluetape4k.workshop.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import io.bluetape4k.io.json.jackson.Jackson
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.zalando.problem.jackson.ProblemModule

/**
 * 예외정보를 Client 로 전달하는 [ProblemModule] 을 [ObjectMapper]에 등록합니다.
 *
 * @constructor Create empty Problem config
 */
@Configuration
@ConditionalOnClass(ProblemModule::class, JsonMapper::class)
class ProblemConfig {

    companion object: KLogging()

    @Bean
    @ConditionalOnMissingBean
    fun objectMapper(): ObjectMapper {
        log.info { "Create ObjectMapper for Problem Library" }

        // 예외의 Stacktrace 정보까지 Client에 전송하기 위한 설정입니다.
        return Jackson.defaultJsonMapper
            .registerModule(ProblemModule().withStackTraces())
    }
}
