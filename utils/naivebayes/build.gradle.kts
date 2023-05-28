configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    api(project(":bluetape4k-coroutines"))
    testImplementation(project(":bluetape4k-tokenizer-korean"))
    testImplementation(project(":bluetape4k-junit5"))

    // Coroutines
    api(Libs.kotlinx_coroutines_core)
    testImplementation(Libs.kotlinx_coroutines_test)
}
