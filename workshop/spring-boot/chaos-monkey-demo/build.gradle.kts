plugins {
    kotlin("plugin.spring")
    id(Plugins.spring_boot)
}

springBoot {
    mainClass.set("io.bluetape4k.workshop.chaos.ApplicationKt")
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    implementation(project(":bluetape4k-spring-support"))
    implementation(project(":bluetape4k-json"))
    implementation(project(":bluetape4k-netty"))
    testImplementation(project(":bluetape4k-junit5"))

    // Chaos Monkey (https://github.com/codecentric/chaos-monkey-spring-boot)
    implementation(Libs.chaos_monkey_spring_boot)

    // Spring Boot
    implementation(Libs.springBoot("autoconfigure"))
    annotationProcessor(Libs.springBoot("autoconfigure-processor"))
    annotationProcessor(Libs.springBoot("configuration-processor"))

    implementation(Libs.springBootStarter("web"))
    implementation(Libs.springBootStarter("jdbc"))
    implementation(Libs.springBootStarter("actuator"))
    implementation(Libs.springBootStarter("aop"))

    testImplementation(Libs.springBootStarter("webflux"))
    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }

    implementation(Libs.h2)
    implementation(Libs.datafaker)
}
