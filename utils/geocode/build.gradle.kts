configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    api(project(":bluetape4k-coroutines"))
    api(project(":bluetape4k-io-json"))
    api(project(":bluetape4k-infra-resilience4j"))
    testImplementation(project(":bluetape4k-junit5"))

    // Google Maps Services
    // https://github.com/googlemaps/google-maps-services-java
    // https://mvnrepository.com/artifact/com.google.maps/google-maps-services
    implementation("com.google.maps:google-maps-services:2.2.0")

    // Bing Map Services (REST API로 사용하므로 feign 을 사용한다)
    implementation(project(":bluetape4k-io-feign"))
    compileOnly(Libs.feign_core)
    compileOnly(Libs.feign_kotlin)
    compileOnly(Libs.feign_slf4j)
    compileOnly(Libs.feign_jackson)

    compileOnly(Libs.httpclient5)
    compileOnly(Libs.httpclient5_cache)
    compileOnly(Libs.httpcore5)

    // Coroutines
    api(Libs.kotlinx_coroutines_core)
    testImplementation(Libs.kotlinx_coroutines_test)
}
