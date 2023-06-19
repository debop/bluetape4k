configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {

    api(project(":bluetape4k-core"))
    api(project(":bluetape4k-io-json"))
    api(project(":bluetape4k-infra-kafka"))
    api(project(":bluetape4k-utils-idgenerators"))

    testImplementation(project(":bluetape4k-junit5"))
    testImplementation(project(":bluetape4k-testcontainers"))

    api(Libs.javers_core)
    testImplementation(Libs.guava)

    // Kafka
    api(Libs.kafka_clients)
    api(Libs.spring_kafka)

    // Codec
    api(Libs.kryo)
    api(Libs.lz4_java)
    compileOnly(Libs.snappy_java)
    compileOnly(Libs.zstd_jni)
}
