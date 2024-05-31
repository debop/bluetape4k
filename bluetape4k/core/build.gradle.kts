plugins {
    kotlin("plugin.allopen")
    id(Plugins.kotlinx_benchmark) version Plugins.Versions.kotlinx_benchmark
}

allOpen {
    // https://github.com/Kotlin/kotlinx-benchmark
    annotation("org.openjdk.jmh.annotations.State")
}
// https://github.com/Kotlin/kotlinx-benchmark
benchmark {
    targets {
        register("test") {
            this as kotlinx.benchmark.gradle.JvmBenchmarkTarget
            jmhVersion = Versions.jmh
        }
    }
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-logging"))
    testImplementation(project(":bluetape4k-junit5"))

    api(Libs.kotlinx_atomicfu)

    // Apache Commons
    api(Libs.commons_lang3)
    api(Libs.commons_codec)
    api(Libs.commons_io)

    // Coroutines
    api(Libs.kotlinx_coroutines_core)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Eclipse Collections
    api(Libs.eclipse_collections)
    compileOnly(Libs.eclipse_collections_forkjoin)
    testImplementation(Libs.eclipse_collections_testutils)

    // Cache
    compileOnly(Libs.caffeine)
    compileOnly(Libs.caffeine_jcache)

    // Compression
    compileOnly(Libs.snappy_java)
    compileOnly(Libs.lz4_java)
    compileOnly(Libs.zstd_jni)
    compileOnly(Libs.commons_compress)
    compileOnly(Libs.xz)

    compileOnly(Libs.brotli4j)
    compileOnly(Libs.brotli4j_native)

    // Binary Serializers
    compileOnly(Libs.kryo)
    compileOnly(Libs.marshalling)
    compileOnly(Libs.marshalling_river)
    compileOnly(Libs.marshalling_serial)
    compileOnly(Libs.fury)                  // NOTE: Fury 가 빠르긴 하지만 BigDecimal, BigInteger 등의 타입을 지원하지 않는다

    // Apple M1
    compileOnly(Libs.jna_platform)

    compileOnly(Libs.netty_transport_native_epoll + ":linux-x86_64")
    compileOnly(Libs.netty_transport_native_kqueue + ":osx-x86_64")
    compileOnly(Libs.netty_transport_native_kqueue + ":osx-aarch_64")

    // Netty 를 Mac M1 에서 사용하기 위한 설정
    compileOnly(Libs.netty_resolver_dns_native_macos + ":osx-aarch_64")

    // Benchmark
    testImplementation(Libs.kotlinx_benchmark_runtime)
    testImplementation(Libs.kotlinx_benchmark_runtime_jvm)
    testImplementation(Libs.jmh_core)

    // Binary Serializer 와 비교하기 하기 위해 Benchmark 에서 사용합니다.
    testImplementation(Libs.jackson_module_kotlin)
}
