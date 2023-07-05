plugins {
    kotlin("plugin.spring")
    kotlin("kapt")

    id(Plugins.spring_boot) version Versions.spring_boot
    id(Plugins.dgs_codegen) version Plugins.Versions.dgs
}

springBoot {
    mainClass.set("io.bluetape4k.workshop.graphql.dgs.ApplicationKt")
}

tasks.withType<com.netflix.graphql.dgs.codegen.gradle.GenerateJavaTask> {
    generateClient = true
    packageName = "io.bluetape4k.workshop.graphql.dgs.generated"
}

tasks.test {
    jvmArgs(
        "--add-opens",
        "java.base/java.lang.reflect=ALL-UNNAMED"
    )
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {

    implementation(project(":bluetape4k-infra-graphql"))
    testImplementation(project(":bluetape4k-junit5"))

    // GraphQL Java
    // Netflix DGS 의 bom (5.5.x) 에서 graph-java 버전이 낮아서 (18.3) 최신 버전 (19.2)으로 강제 update 해야 한다
    // https://github.com/Netflix/dgs-framework/issues/1281#issuecomment-1284694300
    implementation(Libs.graphql_java)

    // GraphQL DGS
    implementation(Libs.graphql_dgs_spring_boot_starter)
    implementation(Libs.graphql_dgs_extended_scalars)

    // Coroutines
    implementation(project(":bluetape4k-coroutines"))
    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Reactor
    implementation(Libs.reactor_core)
    implementation(Libs.reactor_kotlin_extensions)
    testImplementation(Libs.reactor_test)

    // Jackson
    implementation(project(":bluetape4k-io-json"))
    implementation(Libs.jackson_module_kotlin)

    // Spring Boot
    implementation(Libs.springBoot("autoconfigure"))
    kapt(Libs.springBoot("autoconfigure-processor"))
    kapt(Libs.springBoot("configuration-processor"))
    runtimeOnly(Libs.springBoot("devtools"))

    implementation(Libs.springBootStarter("web"))
    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }

    implementation(Libs.datafaker)

    testImplementation(Libs.mockk)
    testImplementation(Libs.springmockk)
}
