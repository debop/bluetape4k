package io.bluetape4k.spring.retrofit2

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import java.util.*

@Configuration
@ConditionalOnClass(Retrofit::class)
class RetrofitAutoConfiguration {

    companion object: KLogging()

    @Configuration
    @EnableRetrofitClients
    class RetrofitClientConfiguration

    @Bean
    fun retrofitContext(specs: Optional<List<RetrofitClientSpecification>>): RetrofitClientContext {
        log.debug { "Create RetrofitClientContext ... specs=$specs" }
        return RetrofitClientContext().apply {
            specs.ifPresent { setConfigurations(it) }
        }
    }
}
