configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-test-junit5"))

    // Vertx
    api(Libs.vertx_core)
    api(Libs.vertx_lang_kotlin)
    api(Libs.vertx_lang_kotlin_coroutines)
    compileOnly(Libs.vertx_web)
    compileOnly(Libs.vertx_web_client)
    compileOnly(Libs.vertx_junit5)

    // Coroutines
    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_jdk8)
    compileOnly(Libs.kotlinx_coroutines_reactive)
    compileOnly(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Netty 를 Mac M1 에서 사용하기 위한 설정
    testImplementation(Libs.netty_resolver_dns_native_macos + ":osx-aarch_64")
}
