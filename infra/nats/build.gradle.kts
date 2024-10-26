configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    compileOnly(project(":bluetape4k-coroutines"))
    testImplementation(project(":bluetape4k-junit5"))
    testImplementation(project(":bluetape4k-testcontainers"))

    // Nats
    api(Libs.jnats)
    api(Libs.nats_spring)
    compileOnly(Libs.nats_spring_cloud_stream_binder)

    // Coroutines
    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Json
    testImplementation(project(":bluetape4k-json"))
    testImplementation(Libs.jackson_databind)
    testImplementation(Libs.jackson_module_kotlin)

    // Compressors & Serializers
    testImplementation(Libs.lz4_java)
    testImplementation(Libs.kryo)
}
