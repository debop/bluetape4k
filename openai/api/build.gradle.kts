plugins {
    // Spring 관련 Plugin 은 spring-cloud-openfeign 예제를 위한 것입니다.
    kotlin("plugin.spring")
//    id(Plugins.spring_boot)
}

//tasks.bootJar {
//    enabled = false
//}
tasks.jar {
    enabled = true
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-json"))
    testImplementation(project(":bluetape4k-junit5"))

    // Json
    api(Libs.jackson_annotations)
    api(Libs.jackson_databind)
    api(Libs.jackson_module_kotlin)

    api(Libs.kotlinx_serialization_json)

    // Coroutines
    api(project(":bluetape4k-coroutines"))
    api(Libs.kotlinx_coroutines_core)
    testImplementation(Libs.kotlinx_coroutines_test)
}
