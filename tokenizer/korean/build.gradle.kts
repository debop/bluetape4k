configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-core"))
    api(project(":bluetape4k-tokenizer-core"))
    testImplementation(project(":bluetape4k-test-junit5"))

    // 한글 분석을 위한 기본적인 통계 정보를 Twitter에서 제공합니다
    // implementation(Libs.twitter_text)
    // testImplementation(Libs.open_korean_text)
    api("com.twitter.twittertext:twitter-text:3.1.0")
    // Benchmark 비교를 위해
    testImplementation("org.openkoreantext:open-korean-text:2.3.1")

    // Coroutines
    compileOnly(Libs.kotlinx_coroutines_core)
    compileOnly(Libs.kotlinx_coroutines_jdk8)
    testImplementation(Libs.kotlinx_coroutines_test)
}
