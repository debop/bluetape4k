plugins {
    id(Plugins.docker_compose) version Plugins.Versions.docker_compose
}

// docker-compose.yml 파일을 참조하여 docker service 를 실행한다.
// 참고: Gradle Docker Compose Plugin (https://github.com/avast/gradle-docker-compose-plugin)
dockerCompose {
    isRequiredBy(tasks.test)
    useComposeFiles.addAll("docker/docker-compose.yml", "docker/docker-compose-postgres.yml")
    startedServices.addAll("redis", "postgres")

    stopContainers.set(true)
    removeContainers.set(true)

    // 복수의 docker-compose.yml 파일을 사용할 경우, 각각의 서비스에 대한 설정을 nested 로 추가할 수 있다.
    // https://github.com/avast/gradle-docker-compose-plugin#nested-configurations
    //    createNested("redis").apply {
    //        useComposeFiles.add("docker/docker-compose-redis.yml")
    //    }
}

tasks.test {
    doFirst {
        dockerCompose.exposeAsEnvironment(this@test)
        dockerCompose.exposeAsSystemProperties(this@test)

        val redisInfo = dockerCompose.servicesInfos["redis"]!!
        systemProperty("redis.url", "redis://${redisInfo.host}:${redisInfo.port}")
        println("redis.url=" + systemProperties["redis.url"])

        val postgresInfo = dockerCompose.servicesInfos["postgres"]!!
        systemProperty("postgres.url", "jdbc:postgresql://${postgresInfo.host}:${postgresInfo.port}/postgres")
        println("postgres.url=" + systemProperties["postgres.url"])
    }
}

dependencies {

    implementation(project(":bluetape4k-core"))
    testImplementation(project(":bluetape4k-junit5"))

    implementation(Libs.redisson)

}
