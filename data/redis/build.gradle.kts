import com.google.protobuf.gradle.id

plugins {
    idea
    id(Plugins.protobuf) version Plugins.Versions.protobuf
    kotlin("plugin.spring")
}

idea {
    module {
        sourceDirs.plus(file("${layout.buildDirectory.asFile.get()}/generated/source/proto/main"))
        testSources.plus(file("${layout.buildDirectory.asFile.get()}/generated/source/proto/test"))
    }
}
// Protobuf Message를 Redis에 저장하는 예제를 위해
// 참고: https://github.com/grpc/grpc-kotlin/blob/master/compiler/README.md
protobuf {
    protoc {
        artifact = Libs.protobuf_protoc
    }
    generateProtoTasks {
        all().forEach { task ->
            // DynamicMessage 사용을 위해
            task.generateDescriptorSet = true
            task.descriptorSetOptions.includeSourceInfo = true
            task.descriptorSetOptions.includeImports = true

            task.builtins {
                // Kotlin DSL 생성
                id("kotlin")
            }
        }
    }
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    api(project(":bluetape4k-netty"))
    compileOnly(project(":bluetape4k-json"))
    compileOnly(project(":bluetape4k-grpc"))
    compileOnly(project(":bluetape4k-coroutines"))
    compileOnly(project(":bluetape4k-cache"))
    compileOnly(project(":bluetape4k-idgenerators"))
    testImplementation(project(":bluetape4k-junit5"))
    testImplementation(project(":bluetape4k-testcontainers"))

    // Redisson
    compileOnly(Libs.redisson)
    compileOnly(Libs.redisson_spring_boot_starter)

    // Lettuce
    compileOnly(Libs.lettuce_core)

    // Spring Data Redis
    compileOnly(Libs.springData("redis"))
    compileOnly(Libs.springBootStarter("data-redis"))

    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    // Codecs
    compileOnly(Libs.kryo)
    compileOnly(Libs.marshalling)
    compileOnly(Libs.marshalling_river)
    compileOnly(Libs.marshalling_serial)
    compileOnly(Libs.fury)

    compileOnly(Libs.jackson_dataformat_protobuf)

    // Json
    compileOnly(project(":bluetape4k-json"))
    compileOnly(Libs.jackson_module_kotlin)

    // Compressor
    compileOnly(Libs.snappy_java)
    compileOnly(Libs.lz4_java)
    compileOnly(Libs.zstd_jni)

    // Protobuf
    compileOnly(Libs.protobuf_java)
    compileOnly(Libs.protobuf_java_util)
    compileOnly(Libs.protobuf_kotlin)

    compileOnly(Libs.javax_cache_api)
    compileOnly(Libs.caffeine)
    compileOnly(Libs.caffeine_jcache)

    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Redisson Map Read/Write Through 예제를 위해 
    testImplementation(project(":bluetape4k-jdbc"))
    testRuntimeOnly(Libs.h2_v2)
    testImplementation(Libs.hikaricp)
    testImplementation(Libs.springBootStarter("jdbc"))
}
