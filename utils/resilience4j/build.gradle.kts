configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    compileOnly(project(":bluetape4k-kotlinx-coroutines"))
    testImplementation(project(":bluetape4k-test-junit5"))

    // Resilience4j
    api(Libs.resilience4j_all)
    api(Libs.resilience4j_cache)
    api(Libs.resilience4j_kotlin)
    compileOnly(Libs.resilience4j_reactor)
    compileOnly(Libs.resilience4j_micrometer)

    // Coroutines
    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_jdk8)
    compileOnly(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // JCache for Resilience4j Cache
    testImplementation(Libs.caffeine_jcache)
    testImplementation(Libs.cache2k_jcache)
}