// NOTE: compileOnly 나 runtimeOnly로 지정된 Dependency를 testImplementation 으로도 지정하도록 합니다.
configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    implementation(Libs.kotlin_reflect)

    api(Libs.slf4j_api)
    implementation(Libs.jcl_over_slf4j)
    compileOnly(Libs.logback)

    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_slf4j)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Testing
    testImplementation(Libs.kluent)
    testImplementation(Libs.mockk)
    testImplementation(Libs.awaitility_kotlin)

    // Property baesd test
    testImplementation(Libs.datafaker)
    testImplementation(Libs.random_beans)
}
