plugins {
    kotlin("plugin.spring")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}


dependencies {
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-junit5"))

    api(Libs.jjwt_api)
    api(Libs.jjwt_impl)
    api(Libs.jjwt_jackson)

    // Jackson
    api(project(":bluetape4k-io-json"))
    api(Libs.jackson_module_kotlin)

    // Compressor
    compileOnly(project(":bluetape4k-io"))
    compileOnly(Libs.lz4_java)
    compileOnly(Libs.snappy_java)
    compileOnly(Libs.zstd_jni)

    // Serialization
    compileOnly(Libs.kryo)

    // Caching
    compileOnly(project(":bluetape4k-infra-cache"))
    testImplementation(Libs.caffeine_jcache)
    testImplementation(Libs.ehcache)

    // Id Generators
    api(project(":bluetape4k-utils-idgenerators"))
    api(Libs.java_uuid_generator)

    // KeyChain을 Redis 나 MongoDB에 저장하여, 다중서버가 공유하기 위한 KeyChainPersister 를 사용하기 위해
    compileOnly(Libs.redisson)
    compileOnly(Libs.mongodb_driver_sync)
    compileOnly(Libs.mongodb_driver_reactivestreams)

    testImplementation(project(":bluetape4k-testcontainers"))
    testImplementation(Libs.testcontainers)
    testImplementation(Libs.testcontainers_mongodb)
}
