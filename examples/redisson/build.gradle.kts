plugins {
    idea
    kotlin("plugin.spring")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-data-redis"))
    implementation(project(":bluetape4k-io-json"))
    implementation(project(":bluetape4k-io-grpc"))
    implementation(project(":bluetape4k-kotlinx-coroutines"))
    implementation(project(":bluetape4k-utils-cache"))
    testImplementation(project(":bluetape4k-utils-idgenerators"))
    testImplementation(project(":bluetape4k-test-junit5"))
    testImplementation(project(":bluetape4k-test-testcontainers"))

    // Redisson
    api(Libs.redisson)
    api(Libs.redisson_spring_boot_starter)

    // Codecs
    implementation(Libs.kryo)
    implementation(Libs.marshalling)
    implementation(Libs.marshalling_river)
    implementation(Libs.marshalling_serial)

    implementation(Libs.jackson_dataformat_protobuf)
    implementation(Libs.jackson_module_kotlin)

    // Compressor
    implementation(Libs.snappy_java)
    implementation(Libs.lz4_java)
    implementation(Libs.zstd_jni)

    // Protobuf
    implementation(Libs.protobuf_java)
    implementation(Libs.protobuf_java_util)
    implementation(Libs.protobuf_kotlin)

    implementation(Libs.javax_cache_api)
    implementation(Libs.caffeine)
    implementation(Libs.caffeine_jcache)

    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_jdk8)
    implementation(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Redisson Map Read/Write Through 예제를 위해 
    testImplementation(project(":bluetape4k-data-jdbc"))
    testRuntimeOnly(Libs.h2)
    testImplementation(Libs.hikaricp)
    testImplementation(Libs.springBootStarter("jdbc"))

    testImplementation(Libs.springBootStarter("data-redis"))
    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

}
