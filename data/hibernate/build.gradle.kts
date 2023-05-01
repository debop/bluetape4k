plugins {
    idea
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
    kotlin("plugin.jpa")
    kotlin("plugin.spring")
    kotlin("kapt")
}

// JPA Entities 들을 Java와 같이 모두 override 가능하게 합니다 (Kotlin 은 기본이 final 입니다)
// 이렇게 해야 association의 proxy 가 만들어집니다.
// https://kotlinlang.org/docs/reference/compiler-plugins.html
allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

kapt {
    showProcessorStats = true
    // kapt 가 제대로 동작하지 않는 경우, 아래 옵션을 제거해보세요.
    // correctErrorTypes = true
}

idea {
    module {
        val kaptMain = file("build/generated/source/kapt/main")
        sourceDirs.plus(kaptMain)
        generatedSourceDirs.plus(kaptMain)

        val kaptTest = file("build/generated/source/kapt/test")
        testSources.plus(kaptTest)
    }
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
    create("testJar")
}


// 테스트 코드를 Jar로 만들어서 다른 프로젝트에서 참조할 수 있도록 합니다.
tasks.register<Jar>("testJar") {
    dependsOn(tasks.testClasses)
    archiveClassifier.set("test")
    from(sourceSets.test.get().output)
}

artifacts {
    add("testJar", tasks["testJar"])
}

// tasks.test { dependsOn(tasks["testJar"]) }

dependencies {
    api(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-junit5"))

    // NOTE: Java 9+ 환경에서 kapt가 제대로 동작하려면 javax.annotation-api 를 참조해야 합니다.
    api(Libs.javax_annotation_api)

    api(Libs.javax_persistence_api)
    api(Libs.hibernate_core)
    testImplementation(Libs.hibernate_testing)

    // NOTE: Querydsl 과 같이 사용할 시에 가끔 예외를 일으킨다. 
    // kapt(Libs.hibernate_jpamodelgen)
    // kaptTest(Libs.hibernate_jpamodelgen)

    // Querydsl
    compileOnly(Libs.querydsl_jpa)
    // Hibernate 6+ jakarta 용은 claasifier로 ":jpa" 대신 ":jakarta" 를 사용해야 합니다.
    // https://github.com/querydsl/querydsl/issues/3493
    kapt(Libs.querydsl_apt + ":jpa")
    kaptTest(Libs.querydsl_apt + ":jpa")

    api(Libs.javax_el_api)
    api(Libs.javax_el)

    // Validator
    api(Libs.javax_validation_api)
    compileOnly(Libs.hibernate_validator)

    // Converter
    compileOnly(project(":bluetape4k-io"))
    compileOnly(project(":bluetape4k-io-cryptography"))
    compileOnly(project(":bluetape4k-io-json"))

    testImplementation(Libs.kryo)
    testImplementation(Libs.marshalling)
    testImplementation(Libs.marshalling_river)

    testImplementation(Libs.snappy_java)
    testImplementation(Libs.lz4_java)

    compileOnly(project(":bluetape4k-utils-idgenerators"))

    // TODO: querydsl-kotlin-codegen 은 tree entity 도 못 만들고, spring-data-jpa 의 repository에서 문제가 생긴다.
    // https://github.com/querydsl/querydsl/issues/3454
    // kapt(Libs.querydsl_kotlin_codegen)
    // kaptTest(Libs.querydsl_kotlin_codegen)

    compileOnly(Libs.springBootStarter("data-jpa"))
    testImplementation(Libs.springBoot("autoconfigure"))
    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    testImplementation(Libs.hikaricp)
    testImplementation(Libs.h2_v2)
    testImplementation(Libs.mysql_connector_j)

    testImplementation(project(":bluetape4k-testcontainers"))
    testImplementation(Libs.testcontainers_mysql)

    // Caching 테스트
    testImplementation(project(":bluetape4k-infra-cache"))
    testImplementation(Libs.springBootStarter("cache"))
    testImplementation(Libs.caffeine)
    testImplementation(Libs.caffeine_jcache)

    // JDBC 와 같이 사용
    testImplementation(project(":bluetape4k-data-jdbc"))
    testImplementation(project(":bluetape4k-vertx-sqlclient"))
}
