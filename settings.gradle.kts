pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version ("0.7.0")
    }
}

val PROJECT_NAME = "bluetape4k"

rootProject.name = "$PROJECT_NAME-root"

includeModules(PROJECT_NAME, false)

includeModules("aws", withBaseDir = true)
includeModules("data", withBaseDir = false)
includeModules("infra", withBaseDir = false)
includeModules("io", withBaseDir = false)
includeModules("javers", withBaseDir = true)
includeModules("openai", withBaseDir = true)
includeModules("quarkus", withBaseDir = true)
includeModules("spring", withBaseDir = true)
includeModules("tokenizer", withBaseDir = true)
includeModules("utils", withBaseDir = false)
includeModules("vertx", withBaseDir = true)

// Examples (library style examples)
includeModules("examples", false, true)
// Workshop (application style examples)

includeModules("workshop/docker", false, false)
includeModules("workshop/graphql", false, false)
includeModules("workshop/kafka", false, false)
includeModules("workshop/quarkus", false, false)
includeModules("workshop/spring-boot", false, false)
includeModules("workshop/spring-cloud", false, false)
includeModules("workshop/spring-data", false, false)
includeModules("workshop/spring-security", false, false)
includeModules("workshop/vertx", false, false)

fun includeModules(baseDir: String, withProjectName: Boolean = true, withBaseDir: Boolean = true) {
    files("$rootDir/$baseDir").files
        .filter { it.isDirectory }
        .forEach { moduleDir ->
            moduleDir.listFiles()
                ?.filter { it.isDirectory }
                ?.forEach { dir ->
                    val basePath = baseDir.replace("/", "-")
                    val projectName = when {
                        !withProjectName && !withBaseDir -> dir.name
                        withProjectName && !withBaseDir  -> PROJECT_NAME + "-" + dir.name
                        withProjectName                  -> PROJECT_NAME + "-" + basePath + "-" + dir.name
                        else                             -> basePath + "-" + dir.name
                    }
                    // println("include modules: $projectName")

                    include(projectName)
                    project(":$projectName").projectDir = dir
                }
        }
}
