plugins {
    id(Plugins.quarkus)
    kotlin("plugin.allopen")

    // NOTE: Quarkus 에서는 JPA 용 Entity를 open 으로 변경하는 것이 작동하지 않는다.
    // NOTE: 아럐 annotation("javax.persistence.Entity") 를 추가해주어야 한다
    kotlin("plugin.jpa")
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
    implementation(platform(Libs.quarkus_universe_bom))

//    api(Libs.jakarta_persistence_api)
//    api(Libs.jakarta_validation_api)

    implementation(project(":bluetape4k-core"))
    implementation(project(":bluetape4k-quarkus-kotlin"))
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
    testImplementation(Libs.kotlinx_coroutines_test)

    // jackson
    implementation(project(":bluetape4k-io-json"))
}
