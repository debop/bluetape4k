plugins {
    id(Plugins.quarkus)

    // Quarkus 는 기본적으로 root project 기반이라 다른 모듈에서 inject 할 beans를 검색하지 않는다
    // 다른 모듈에서도 beans 를 검색하고, inject 되도록 하기 위해 jandex plugins 를 사용해서 bean 목록을 제공해야 한다
    // 참고: https://quarkus.io/guides/gradle-tooling#publishing-your-application
    // id(Plugins.jandex) version Plugins.Versions.jandex
    // ==> empty beans.xml 을 사용하는 것으로 변경
    // https://stackoverflow.com/questions/55513502/how-to-create-a-jandex-index-in-quarkus-for-classes-in-a-external-module

    kotlin("plugin.allopen")
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
    annotation("javax.persistence.Entity")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    implementation(enforcedPlatform(Libs.quarkus_bom))

    api(project(":bluetape4k-core"))
    api(project(":bluetape4k-utils-mutiny"))
    api(project(":bluetape4k-vertx-core"))
    compileOnly(project(":bluetape4k-testcontainers"))
    compileOnly(project(":bluetape4k-junit5"))

    api(Libs.javax_annotation_api)
    api(Libs.javax_inject)
    api(Libs.javax_interceptor_api)
    api(Libs.javax_persistence_api)
    api(Libs.javax_transaction_api)

    api(Libs.quarkus_arc)
    api(Libs.quarkus_kotlin)
    compileOnly(Libs.quarkus_arc)
    compileOnly(Libs.quarkus_hibernate_reactive_panache)
    compileOnly(Libs.quarkus_junit5)
    compileOnly(Libs.quarkus_vertx)
    compileOnly(Libs.quarkus_reactive_routes)

    api(Libs.vertx_lang_kotlin)
    api(Libs.vertx_lang_kotlin_coroutines)

    compileOnly(Libs.rest_assured_kotlin)
    compileOnly(Libs.camel_quarkus_kotlin)

    // Coroutines
    api(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_jdk8)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Testing
    compileOnly(Libs.rest_assured_kotlin)
    compileOnly(Libs.awaitility_kotlin)

    // TestResource
    compileOnly(Libs.testcontainers_kafka)
    compileOnly(Libs.testcontainers_mysql)

    testImplementation(Libs.kafka_clients)
    testImplementation(Libs.hikaricp)
    testImplementation(Libs.mysql_connector_j)

    // Redis
    compileOnly(Libs.quarkus("redis-client"))
    testImplementation(Libs.redisson("redisson-quarkus-20"))
}
