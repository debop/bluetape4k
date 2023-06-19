configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {

    api(project(":bluetape4k-core"))
    api(project(":bluetape4k-io-json"))
    api(project(":bluetape4k-utils-idgenerators"))
    compileOnly(project(":bluetape4k-data-hibernate"))
    compileOnly(project(":bluetape4k-infra-cache"))

    testImplementation(project(":bluetape4k-junit5"))
    testImplementation(project(":bluetape4k-testcontainers"))

    api(Libs.javers_core)
    testImplementation(Libs.guava)

    // Redis
    compileOnly(project(":bluetape4k-data-redis"))
    api(Libs.lettuce_core)
    compileOnly(Libs.redisson)

    // Codec
    api(Libs.kryo)
    api(Libs.lz4_java)
    compileOnly(Libs.snappy_java)
    compileOnly(Libs.zstd_jni)
}
