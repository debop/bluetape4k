configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    api(project(":bluetape4k-kotlinx-coroutines"))
    testImplementation(project(":bluetape4k-test-junit5"))
    testImplementation(project(":bluetape4k-utils-idgenerators"))

    compileOnly(Libs.kotlinx_atomicfu)

    // Coroutines
    api(Libs.kotlinx_coroutines_core)
    api(Libs.kotlinx_coroutines_jdk8)
    api(Libs.kotlinx_coroutines_slf4j)
    compileOnly(Libs.kotlinx_coroutines_jdk9)
    testImplementation(Libs.kotlinx_coroutines_debug)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Coroutines Flow를 Reactor처럼 테스트 할 수 있도록 해줍니다.
    // 참고: https://github.com/cashapp/turbine/
    testImplementation(Libs.turbine)
}
