configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-openai-tiktoken"))

    api(project(":bluetape4k-http"))
    api(project(":bluetape4k-retrofit2"))

    api(project(":bluetape4k-json"))

    // SSE
    api(Libs.okhttp3)
    api(Libs.okhttp3_sse)
    api(Libs.retrofit2_converter_jackson)
    api(Libs.retrofit2_adapter_reactor)

    // Coroutines
    api(project(":bluetape4k-coroutines"))
    api(Libs.kotlinx_coroutines_core)
    api(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)
}
