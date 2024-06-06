plugins {
    kotlin("plugin.allopen") version Versions.kotlin
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
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-junit5"))

    // Hyperscan
    val hyperscanVersion = "5.4.11-2.0.0"  // https://mvnrepository.com/artifact/com.gliwka.hyperscan/native
    api("com.gliwka.hyperscan:native:$hyperscanVersion")
    api("com.gliwka.hyperscan:native:$hyperscanVersion:linux-x86_64")
    api("com.gliwka.hyperscan:native:$hyperscanVersion:linux-arm64")
    api("com.gliwka.hyperscan:native:$hyperscanVersion:macosx-x86_64")
    api("com.gliwka.hyperscan:native:$hyperscanVersion:macosx-arm64")
    api("org.bytedeco:javacpp:1.5.9")  // https://github.com/bytedeco/javacpp


    // Benchmark
    testImplementation(Libs.kotlinx_benchmark_runtime)
    testImplementation(Libs.kotlinx_benchmark_runtime_jvm)
    testImplementation(Libs.jmh_core)
}
