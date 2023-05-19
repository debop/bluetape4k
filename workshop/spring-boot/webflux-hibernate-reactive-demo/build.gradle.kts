plugins {
    kotlin("plugin.spring")
    kotlin("plugin.allopen")
    kotlin("plugin.jpa")
    kotlin("kapt")

    id(Plugins.spring_boot)
}

springBoot {
    mainClass.set("io.bluetape4k.workshop.webflux.hibernate.reactive.HibernateReactiveApplicationKt")
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
    // kapt 가 제대로 동작하지 않는 경우, 해당 클래스를 약간 수정해보세요. (Comments 추가 등으로)
    correctErrorTypes = true
}

configurations {
    testImplementation.get().extendsFrom(compileOnly.get(), runtimeOnly.get())
}

dependencies {
    api(project(":bluetape4k-data-hibernate-reactive"))
    api(project(":bluetape4k-utils-mutiny"))
    api(project(":bluetape4k-vertx-core"))
    api(project(":bluetape4k-spring-support"))
    implementation(project(":bluetape4k-io-json"))
    implementation(project(":bluetape4k-io-netty"))
    testImplementation(project(":bluetape4k-junit5"))

    // Hibernate Reactive
    implementation(Libs.hibernate_reactive_core)
    implementation(Libs.vertx_mysql_client) // MySQL

    // hibernate-reactive 는 querydsl 을 사용하지 못한다. 대신 jpamodelgen 을 사용합니다.
//    kapt(Libs.hibernate_jpamodelgen)
//    kaptTest(Libs.hibernate_jpamodelgen)

    // MySQL Container
    implementation(project(":bluetape4k-testcontainers"))
    implementation(Libs.testcontainers_mysql)
    // Testcontainers MySQL 에서 검증을 위해 사용하기 위해 불가피하게 필요합니다
    // reactive 방식에서는 항상 verx-mysql-client 를 사용합니다
    runtimeOnly(Libs.hikaricp)
    runtimeOnly(Libs.mysql_connector_j)

    // Validator
    implementation(Libs.hibernate_validator)
    implementation(Libs.javax_validation_api)
    implementation(Libs.javax_el)

    // Spring Boot
    implementation(Libs.springBoot("autoconfigure"))
    kapt(Libs.springBoot("autoconfigure-processor"))
    kapt(Libs.springBoot("configuration-processor"))
    runtimeOnly(Libs.springBoot("devtools"))

    implementation(Libs.springBootStarter("webflux"))
    implementation(Libs.springBootStarter("actuator"))
    implementation(Libs.springBootStarter("validation"))

    testImplementation(Libs.springBootStarter("test")) {
        exclude(group = "junit", module = "junit")
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }

    // Coroutines
    implementation(project(":bluetape4k-coroutines"))
    implementation(Libs.kotlinx_coroutines_core)
    implementation(Libs.kotlinx_coroutines_jdk8)
    implementation(Libs.kotlinx_coroutines_reactor)
    testImplementation(Libs.kotlinx_coroutines_test)

    // Reactor
    implementation(Libs.netty_all)
    implementation(Libs.reactor_netty)
    implementation(Libs.reactor_kotlin_extensions)
    testImplementation(Libs.reactor_test)
}
