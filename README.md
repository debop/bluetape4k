# BlueTape4k

![Blue Tape](./doc/bluetape4k.png)

Kotlin 언어로 JVM 환경에서 개발할 때 사용할 청테이프용 라이브러리

## 참조 방법

Gradle 프로젝트의 buildSrc 모듈에 다음과 같이 작성합니다.

```kotlin

object Versions {
    // ...
    const val bluetape4k = "1.0.1"
    // ...
}

object Libs {

    // bluetape4k
    fun bluetape4k(module: String, version: String = Versions.bluetape4k) = "io.bluetape4k:bluetape4k-$module:$version"

    val bluetape4k_bom = bluetape4k("bom")

    val bluetape4k_core = bluetape4k("core")
    val bluetape4k_coroutines = bluetape4k("coroutines")
    val bluetape4k_logging = bluetape4k("logging")
    val bluetape4k_junit5 = bluetape4k("junit5")
    val bluetape4k_testcontainers = bluetape4k("testcontainers")

    // IO
    val bluetape4k_avro = bluetape4k("avro")
    val bluetape4k_cryptography = bluetape4k("cryptography")
    val bluetape4k_csv = bluetape4k("csv")
    val bluetape4k_feign = bluetape4k("feign")
    val bluetape4k_grpc = bluetape4k("grpc")
    val bluetape4k_http = bluetape4k("http")
    val bluetape4k_json = bluetape4k("json")
    val bluetape4k_netty = bluetape4k("netty")
    val bluetape4k_retrofit2 = bluetape4k("retrofit2")

    // UTILS
    val bluetape4k_geocode = bluetape4k("geocode")
    val bluetape4k_geoip2 = bluetape4k("geoip2")
    val bluetape4k_idgenerators = bluetape4k("idgenerators")
    val bluetape4k_images = bluetape4k("images")
    val bluetape4k_jwt = bluetape4k("jwt")
    val bluetape4k_math = bluetape4k("math")
    val bluetape4k_money = bluetape4k("money")
    val bluetape4k_times = bluetape4k("times")
    val bluetape4k_units = bluetape4k("unit")

    // Data
    val bluetape4k_cassandra = bluetape4k("cassandra")
    val bluetape4k_hibernate = bluetape4k("hibernate")
    val bluetape4k_hibernate_reactive = bluetape4k("hibernate-reactive")
    val bluetape4k_jdbc = bluetape4k("jdbc")
    val bluetape4k_r2dbc = bluetape4k("r2dbc")
    val bluetape4k_redis = bluetape4k("redis")

    // Infrastructure
    val bluetape4k_bucket4j = bluetape4k("bucket4j")
    val bluetape4k_cache = bluetape4k("cache")
    val bluetape4k_graphql = bluetape4k("graphql")
    val bluetape4k_kafka = bluetape4k("kafka")
    val bluetape4k_micrometer = bluetape4k("micrometer")
    val bluetape4k_nats = bluetape4k("nats")
    val bluetape4k_otel = bluetape4k("otel")
    val bluetape4k_resilience4j = bluetape4k("resilience4j")

    // Spring
    val bluetape4k_spring_cassandra = bluetape4k("spring-cassandra")
    val bluetape4k_spring_jpa = bluetape4k("spring-jpa")
    val bluetape4k_spring_retrofit2 = bluetape4k("spring-retrofit2")
    val bluetape4k_spring_security = bluetape4k("spring-security")
    val bluetape4k_spring_support = bluetape4k("spring-support")

    // AWS
    val bluetape4k_aws_core = bluetape4k("aws-core")
    val bluetape4k_aws_dynamodb = bluetape4k("aws-dynamodb")
    val bluetape4k_aws_s3 = bluetape4k("aws-s3")
    val bluetape4k_aws_ses = bluetape4k("aws-ses")
    val bluetape4k_aws_sqs = bluetape4k("aws-sqs")

    // ...
}
```

다음으로 참조하고자 하는 프로젝트의 `build.gradle.kts` 에 다음과 같이 참조를 추가합니다.

```kotlin
dependencies {

    api(Libs.bluetape4k_core)
    api(Libs.bluetape4k_coroutines)
    api(Libs.bluetape4k_idgenerators)
    testImplementation(Libs.bluetape4k_junit5)

    // Coroutines
    testImplementation(Libs.kotlinx_coroutines_test)
}
```
