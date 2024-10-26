plugins {
    kotlin("plugin.spring")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {

    api(project(":bluetape4k-core"))
    api(project(":bluetape4k-json"))
    api(project(":bluetape4k-idgenerators"))
    compileOnly(project(":bluetape4k-hibernate"))
    compileOnly(project(":bluetape4k-redis"))
    compileOnly(project(":bluetape4k-cache"))

    testImplementation(project(":bluetape4k-junit5"))
    testImplementation(project(":bluetape4k-testcontainers"))

    // Javers
    api(project(":bluetape4k-javers-core"))
    // bluetape4k-javers-core 의 테스트 코드를 재활용하기 위해 참조합니다.
    testImplementation(project(path = ":bluetape4k-javers-core", configuration = "testJar"))
    api(Libs.javers_core)
    api(Libs.javers_spring)
    testImplementation(Libs.guava)

    // Codec
    compileOnly(Libs.kryo)
    compileOnly(Libs.fury)
    compileOnly(Libs.snappy_java)
    compileOnly(Libs.lz4_java)
    compileOnly(Libs.zstd_jni)
}
