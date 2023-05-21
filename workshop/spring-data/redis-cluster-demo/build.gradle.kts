plugins {
    kotlin("plugin.spring")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    implementation(project(":bluetape4k-data-redis"))
    implementation(project(":bluetape4k-coroutines"))
    implementation(project(":bluetape4k-io-json"))
    implementation(project(":bluetape4k-utils-idgenerators"))
    testImplementation(project(":bluetape4k-junit5"))
    implementation(project(":bluetape4k-testcontainers"))

    // spring-data-redis에서는 기본적으로 lettuce를 사용합니다.
    // Redisson
    // implementation(Libs.redisson)
    // https://github.com/redisson/redisson/blob/master/redisson-spring-data/README.md
    // spring-data-redis 2.7.x 를 사용하므로, redisson도 같은 버전을 참조해야 한다
    // implementation(Libs.redisson_spring_data_27)

    // Codecs
    implementation(Libs.kryo)
    // Compressor
    implementation(Libs.lz4_java)

    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    implementation(Libs.lettuce_core)
    implementation(Libs.commons_pool2)
    implementation(Libs.springBootStarter("data-redis"))

    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation(Libs.reactor_test)
}
