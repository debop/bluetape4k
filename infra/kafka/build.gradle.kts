configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    api(project(":bluetape4k-io-json"))
    api(project(":bluetape4k-infra-resilience4j"))
    testImplementation(project(":bluetape4k-junit5"))
    testImplementation(project(":bluetape4k-testcontainers"))

    // Kafka
    api(Libs.kafka_clients)
    api(Libs.kafka_streams)
    compileOnly(Libs.spring_kafka)
    compileOnly(Libs.kafka_generator)
    testImplementation(Libs.kafka_streams_test_utils)
    testImplementation(Libs.kafka_server_common)
    testImplementation(Libs.testcontainers_kafka)

    // Codecs
    compileOnly(Libs.kryo)
    compileOnly(Libs.marshalling)
    compileOnly(Libs.marshalling_river)
    compileOnly(Libs.marshalling_serial)

    // Compressors
    compileOnly(Libs.snappy_java)
    compileOnly(Libs.lz4_java)
    compileOnly(Libs.zstd_jni)

    // Coroutines
    api(project(":bluetape4k-coroutines"))
    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_jdk8)
    testImplementation(Libs.kotlinx_coroutines_test)

}
