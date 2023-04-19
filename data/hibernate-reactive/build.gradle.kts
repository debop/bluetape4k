plugins {
    kotlin("plugin.spring")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
    kotlin("plugin.jpa")
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

// NOTE: implementation 로 지정된 Dependency를 testImplementation 으로도 지정하도록 합니다.
configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-data-hibernate"))
    api(project(":bluetape4k-kotlinx-mutiny"))
    api(project(":bluetape4k-vertx-core"))

    api(Libs.hibernate_reactive_core)

    // hibernate-reactive 는 querydsl 을 사용하지 못한다. 대신 jpamodelgen 을 사용합니다.
    kapt(Libs.hibernate_jpamodelgen)
    kaptTest(Libs.hibernate_jpamodelgen)

    implementation(Libs.hibernate_validator)
    runtimeOnly(Libs.jakarta_el)

    api(Libs.mutiny_kotlin)
    api(Libs.kotlinx_coroutines_jdk8)
    testImplementation(Libs.kotlinx_coroutines_test)

    testImplementation(Libs.vertx_mysql_client) // MySQL
    // Testcontainers MySQL 에서 검증을 위해 사용하기 위해 불가피하게 필요합니다
    // reactive 방식에서는 항상 verx-mysql-client 를 사용합니다
    testRuntimeOnly(Libs.mysql_connector_java)

    testImplementation(project(":bluetape4k-test-junit5"))
    testImplementation(project(":bluetape4k-test-testcontainers"))
    testImplementation(Libs.testcontainers_mysql)

    // kommons-hibernate의 entity 들을 재사용하려고 testArchives 를 참조한다
    // persistence.xml 에서도 jar-file에 entity path를 추가해야 한다
    // see : https://github.com/hauner/gradle-plugins/tree/master/jartest
    // testImplementation(project(path = ":bluetape4k-data-hibernate", configuration = "testArchives"))

    // LifecycleEntity가 spring-data-jpa 의 @AuditingEntityListener를 사용해서 어쩔 수 없이 추가했다.
    // 실제로 사용 안한다
    testImplementation(Libs.springBootStarter("data-jpa"))
}
