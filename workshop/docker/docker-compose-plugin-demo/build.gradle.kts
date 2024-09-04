import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    id(Plugins.docker_compose) version Plugins.Versions.docker_compose
}

// docker-compose.yml 파일을 참조하여 docker service 를 실행한다.
// 참고: Gradle Docker Compose Plugin (https://github.com/avast/gradle-docker-compose-plugin)
dockerCompose {
    isRequiredBy(tasks.test)

    // NOTE: docker-compose-plugin 이 MacOS 에서는 `docker` 명령어를 찾지 못하는 문제를 해결한다.
    // 보통 MacOS 에서는 `/opt/homebrew/bin/docker-compose`, `/usr/local/bin/docker-compose` 에 설치되어 있으므로,
    // Works around an issue where the docker command is not found.
    // Falls back to the default, which may work on some platforms.
    // https://github.com/avast/gradle-docker-compose-plugin/issues/435
    // https://github.com/gradle/gradle/issues/10483
    if (DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX) {
        listOf("/opt/homebrew/bin/docker-compose").firstOrNull {
            File(it).exists()
        }?.let { docker ->
            executable.set(docker)
        }
        listOf("/opt/homebrew/bin/docker").firstOrNull {
            File(it).exists()
        }?.let { docker ->
            dockerExecutable.set(docker)
        }

        listOf("/usr/bin/docker-compose", "/usr/local/bin/docker-compose").firstOrNull {
            File(it).exists()
        }?.let { docker ->
            executable.set(docker)
        }
        listOf("/usr/bin/docker", "/usr/local/bin/docker").firstOrNull {
            File(it).exists()
        }?.let { docker ->
            dockerExecutable.set(docker)
        }
    }

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
        // 제대로 시스템 속성에 설정되었는지 확인한다 (예: redis.url, postgres.url)
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
