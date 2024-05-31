plugins {
    id(Plugins.quarkus)
    kotlin("plugin.allopen")

    // NOTE: Quarkus 에서는 JPA 용 Entity를 open 으로 변경하는 것이 작동하지 않는다.
    // NOTE: 아럐 annotation("javax.persistence.Entity") 를 추가해주어야 한다
    kotlin("plugin.jpa")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")

    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    // NOTE: Quarkus 는 꼭 gradle platform 으로 참조해야 제대로 빌드가 된다.
    implementation(platform(Libs.quarkus_bom))
    implementation(platform(Libs.quarkus_universe_bom))

    implementation(project(":bluetape4k-core"))
    implementation(project(":bluetape4k-quarkus-kotlin"))
    implementation("io.quarkus:quarkus-opentelemetry:3.3.2")
    implementation("io.quarkus:quarkus-opentelemetry:3.3.2")
    implementation("io.quarkus:quarkus-opentelemetry:3.3.2")
    implementation("io.quarkus:quarkus-opentelemetry:3.3.2")
    testImplementation(project(":bluetape4k-junit5"))

    implementation(Libs.quarkus("hibernate-reactive-panache"))
    implementation(Libs.quarkus("hibernate-validator"))
    implementation(Libs.quarkus("kotlin"))
    implementation(Libs.quarkus("vertx"))
    implementation(Libs.quarkus("smallrye-openapi"))
    implementation(Libs.quarkus("resteasy-reactive"))
    implementation(Libs.quarkus("resteasy-reactive-kotlin"))
    implementation(Libs.quarkus("resteasy-reactive-jackson"))

    // see: https://quarkus.io/guides/datasource
    // rective datasource 는 mysql, postres 밖에 없다
    implementation(Libs.quarkus("reactive-mysql-client"))
    // implementation(Libs.quarkus("reactive-pg-client"))

    testImplementation(Libs.quarkus("junit5"))
    testImplementation(Libs.rest_assured_kotlin)

    // coroutines
    implementation(project(":bluetape4k-coroutines"))
    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_reactive)
    testImplementation(Libs.kotlinx_coroutines_test)

    // jackson
    implementation(project(":bluetape4k-json"))
}
