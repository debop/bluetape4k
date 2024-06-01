configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-json"))
    testImplementation(project(":bluetape4k-junit5"))

    // MaxMind GeoIP2
    api("com.maxmind.geoip2:geoip2:4.0.1")

    testImplementation(Libs.kotlinx_coroutines_test)
}
