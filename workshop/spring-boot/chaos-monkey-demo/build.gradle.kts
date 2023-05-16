plugins {
    kotlin("plugin.spring")
    kotlin("kapt")
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
    implementation(project(":bluetape4k-io-json"))
    implementation(project(":bluetape4k-io-netty"))
    testImplementation(project(":bluetape4k-junit5"))

    // Chaos Monkey (https://github.com/codecentric/chaos-monkey-spring-boot)
    implementation(Libs.chaos_monkey_spring_boot)

    // Spring Boot
    implementation(Libs.springBoot("autoconfigure"))
    kapt(Libs.springBoot("autoconfigure-processor"))
    kapt(Libs.springBoot("configuration-processor"))

    implementation(Libs.springBootStarter("web"))
    implementation(Libs.springBootStarter("jdbc"))
    implementation(Libs.springBootStarter("actuator"))
    implementation(Libs.springBootStarter("aop"))

    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    implementation(Libs.h2)
    implementation(Libs.datafaker)
}
