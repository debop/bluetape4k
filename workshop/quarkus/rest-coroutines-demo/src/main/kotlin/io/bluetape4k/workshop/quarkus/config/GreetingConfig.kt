package io.bluetape4k.workshop.quarkus.config

import io.smallrye.config.ConfigMapping
import io.smallrye.config.WithDefault
import io.smallrye.config.WithName

/**
 * Configuration 정보를 grouping 할 수 있습니다.
 *
 * 참고: [Config Mappings](https://smallrye.io/docs/smallrye-config/main/mapping/mapping.html)
 */
@ConfigMapping(prefix = "greeting")
interface GreetingConfig {

    @get:WithName("message")
    val message: String

    @get:WithName("suffix")
    @get:WithDefault("!")
    val suffix: String

    @get:WithName("name")
    val name: String?

    /**
     * NOTE: @ConfigProperty 로는 Kotlin Primitive type 을 지원하지 않습니다. @ConfigMapping 을 사용하면 Kotlin primitive type 도 가능합니다.
     *
     * see: [Mapping Configuration to Objects](https://quarkus.io/guides/config-mappings)
     */
    @get:WithName("age")
    @get:WithDefault("54")
    val age: Int?
}
