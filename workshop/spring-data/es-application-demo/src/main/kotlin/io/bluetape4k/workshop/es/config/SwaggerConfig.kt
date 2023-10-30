package io.bluetape4k.workshop.es.config

import io.bluetape4k.support.unsafeLazy
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    fun apiInfo(): OpenAPI {
        return OpenAPI().info(info)
    }

    private val info by unsafeLazy {
        Info().title("Spring Data Elasticsearch Demo")
            .description("Spring Data Elasticsearch Demo with Testcontainers")
            .version("v1")
            .contact(contact)
            .license(license)
    }

    private val contact by unsafeLazy {
        Contact()
            .name("bluetape4k")
            .email("sunghyouk.bae@gmail.com")
            .url("https://github.com/debop/bluetake4k")
    }

    private val license by unsafeLazy {
        License()
            .name("Apache License 2.0")
            .url("http://www.apache.org/licenses/LICENSE-2.0.html")
    }
}
