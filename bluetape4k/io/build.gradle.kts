plugins {
    idea
    kotlin("plugin.allopen")
    id(Plugins.kotlinx_benchmark) version Plugins.Versions.kotlinx_benchmark
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

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
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-junit5"))

    api(Libs.commons_io)
    api(Libs.commons_codec)
    api(Libs.commons_lang3)

    // Compression
    compileOnly(Libs.snappy_java)
    compileOnly(Libs.lz4_java)
    compileOnly(Libs.zstd_jni)
    compileOnly(Libs.commons_compress)
    compileOnly(Libs.xz)
    compileOnly(Libs.brotli4j)

    // Cryptography
    compileOnly(Libs.jasypt)
    compileOnly(Libs.bouncycastle_bcprov)
    compileOnly(Libs.bouncycastle_bcpkix)
    compileOnly(Libs.commons_codec)

    // Binary Serializers
    compileOnly(Libs.kryo)
    compileOnly(Libs.marshalling)
    compileOnly(Libs.marshalling_river)
    compileOnly(Libs.marshalling_serial)

    // Apple M1
    compileOnly(Libs.jna_platform)

    // Coroutines
    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_jdk8)
    testImplementation(Libs.kotlinx_coroutines_test)

    // NOTE: linux-x86_64 를 따로 추가해줘야 제대로 classifier가 지정된다. 이유는 모르겠지만, 이렇게 해야 제대로 된 jar를 참조한다
    compileOnly(Libs.netty_transport_native_epoll + ":linux-x86_64")
    compileOnly(Libs.netty_transport_native_kqueue + ":osx-x86_64")

    // Benchmark
    testImplementation(Libs.kotlinx_benchmark_runtime)
    testImplementation(Libs.kotlinx_benchmark_runtime_jvm)
    testImplementation(Libs.jmh_core)
}
