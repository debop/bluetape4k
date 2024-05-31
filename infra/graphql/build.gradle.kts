plugins {
    kotlin("kapt")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    api(project(":bluetape4k-coroutines"))
    api(project(":bluetape4k-cryptography"))
    api(project(":bluetape4k-json"))
    compileOnly(project(":bluetape4k-cache"))
    testImplementation(project(":bluetape4k-junit5"))

    // Apollo GraphQL
    api(Libs.apollo_runtime)
    api(Libs.apollo_runtime_jvm)
    testImplementation(Libs.apollo_mockserver)
    testImplementation(Libs.apollo_testing_support)

    // GraphQL DGS
    api(Libs.graphql_dgs_client)
    api(Libs.graphql_dgs_reactive)
    compileOnly(Libs.graphql_dgs_webflux_starter)

    // Coroutines
    api(Libs.kotlinx_coroutines_core)
    api(Libs.kotlinx_coroutines_jdk8)
    api(Libs.kotlinx_coroutines_slf4j)
    api(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Cache
    compileOnly(Libs.caffeine)
    compileOnly(Libs.redisson)

    // Spring
    compileOnly(Libs.springSecurity("core"))
}
