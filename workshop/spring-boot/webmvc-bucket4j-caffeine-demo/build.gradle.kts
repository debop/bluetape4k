plugins {
    kotlin("plugin.spring")
    kotlin("kapt")
    id(Plugins.spring_boot)
}

springBoot {
    mainClass.set("io.bluetape4k.workshop.bucket4j.CaffeineApplicationKt")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    // api(project(":bluetape4k-infra-bucket4j"))
    api(project(":bluetape4k-spring-support"))
    implementation(project(":bluetape4k-json"))
    implementation(project(":bluetape4k-netty"))
    testImplementation(project(":bluetape4k-junit5"))

    // Bucket4j
    api(Libs.bucket4j_core)
    api(Libs.bucket4j_caffeine)
    api(Libs.bucket4j_spring_boot)

    api(Libs.javax_cache_api)

    // Caffeine - 로컬 캐시는 AsyncCacheResolver를 구현한 것이 아니므로 Webflux 에서는 사용하지 못한다.
    api(Libs.caffeine)
    api(Libs.caffeine_jcache)

    // Spring Boot
    implementation(Libs.springBoot("autoconfigure"))
    kapt(Libs.springBoot("autoconfigure-processor"))
    kapt(Libs.springBoot("configuration-processor"))
    runtimeOnly(Libs.springBoot("devtools"))

    implementation(Libs.springBootStarter("web"))
    implementation(Libs.springBootStarter("cache"))
    implementation(Libs.springBootStarter("validation"))
    implementation(Libs.springBootStarter("actuator"))

    testImplementation(Libs.springBootStarter("webflux"))
    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }
}
