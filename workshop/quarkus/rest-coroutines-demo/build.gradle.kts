plugins {
    id(Plugins.quarkus)
    kotlin("plugin.allopen")
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    // NOTE: Quarkus 는 꼭 gradle platform 으로 참조해야 제대로 빌드가 된다.
    implementation(platform(Libs.quarkus_bom))
    implementation(platform(Libs.quarkus_universe_bom))

    implementation(project(":bluetape4k-quarkus-kotlin"))
    testImplementation(project(":bluetape4k-junit5"))

    implementation(Libs.quarkus("resteasy-reactive-kotlin"))
    implementation(Libs.quarkus("resteasy-reactive-jackson"))

    // rest client
    implementation(Libs.quarkus("rest-client-reactive"))
    implementation(Libs.quarkus("rest-client-reactive-jackson"))

    testImplementation(Libs.quarkus_junit5)
    testImplementation(Libs.rest_assured_kotlin)

    // coroutines
    implementation(project(":bluetape4k-coroutines"))
    implementation(Libs.kotlinx_coroutines_core)
    testImplementation(Libs.kotlinx_coroutines_test)

    // jackson
    implementation(project(":bluetape4k-io-json"))
}
